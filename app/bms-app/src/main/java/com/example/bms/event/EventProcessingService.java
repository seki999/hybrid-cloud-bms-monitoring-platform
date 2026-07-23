package com.example.bms.event;

import com.example.bms.alert.Alert;
import com.example.bms.alert.AlertHistory;
import com.example.bms.alert.AlertHistoryRepository;
import com.example.bms.alert.AlertRepository;
import com.example.bms.common.domain.AlertStatus;
import com.example.bms.common.domain.Severity;
import com.example.bms.device.Device;
import com.example.bms.device.DeviceRepository;
import com.example.bms.device.OperationalStatus;
import com.example.bms.monitoring.MonitoringRule;
import com.example.bms.monitoring.MonitoringRuleRepository;
import com.example.bms.monitoring.MonitoringTarget;
import com.example.bms.monitoring.MonitoringTargetRepository;
import com.example.bms.monitoring.TargetType;
import com.example.bms.monitoring.TcpPingResult;
import com.example.bms.monitoring.TcpPingResultRepository;
import com.example.bms.notification.NotificationService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 原始监视数据から Event 保存、Alert 聚合、恢复判定、通知触发までを一つの事务中组织する应用服务。
 *
 * <p>协议 Adapter 只负责解析；规则和状态迁移集中在这里，避免 Syslog/SNMP/TCP 各自出现不一致逻辑。</p>
 */
@Service
public class EventProcessingService {
    private static final List<AlertStatus> ACTIVE = List.of(
            AlertStatus.WARNING, AlertStatus.CRITICAL, AlertStatus.ACKNOWLEDGED);
    private final DeviceRepository deviceRepository;
    private final MonitoringTargetRepository targetRepository;
    private final MonitoringRuleRepository ruleRepository;
    private final MonitoringEventRepository eventRepository;
    private final AlertRepository alertRepository;
    private final AlertHistoryRepository historyRepository;
    private final TcpPingResultRepository tcpResultRepository;
    private final NotificationService notificationService;

    public EventProcessingService(DeviceRepository deviceRepository, MonitoringTargetRepository targetRepository,
                                  MonitoringRuleRepository ruleRepository, MonitoringEventRepository eventRepository,
                                  AlertRepository alertRepository, AlertHistoryRepository historyRepository,
                                  TcpPingResultRepository tcpResultRepository,
                                  NotificationService notificationService) {
        this.deviceRepository = deviceRepository;
        this.targetRepository = targetRepository;
        this.ruleRepository = ruleRepository;
        this.eventRepository = eventRepository;
        this.alertRepository = alertRepository;
        this.historyRepository = historyRepository;
        this.tcpResultRepository = tcpResultRepository;
        this.notificationService = notificationService;
    }

    /**
     * 标准化并处理一次观测。
     *
     * @param request 协议无关输入
     * @return 永远保存的一次 Event（包括格式错误或重复标记）
     */
    @Transactional
    public MonitoringEvent process(IngestRequest request) {
        Instant occurredAt = request.occurredAt() == null ? Instant.now() : request.occurredAt();
        Optional<Device> device = deviceRepository.findFirstByHostnameIgnoreCaseOrNameIgnoreCase(
                request.host(), request.host());
        TargetType type = TargetType.valueOf(request.source().name());
        Optional<MonitoringTarget> target = device.flatMap(value ->
                targetRepository.findFirstByDeviceAndTargetTypeAndEnabledTrue(value, type));
        Optional<MonitoringRule> rule = target.flatMap(value ->
                ruleRepository.findFirstByTargetAndMetricNameAndEnabledTrue(value, metricName(request)));

        AlertStatus status = determineStatus(request, rule.orElse(null));
        Severity severity = determineSeverity(request, status);
        String fingerprint = fingerprint(request);
        int suppressionSeconds = rule.map(MonitoringRule::getSuppressionSeconds).orElse(300);
        boolean duplicate = eventRepository.existsByFingerprintAndOccurredAtAfter(fingerprint,
                occurredAt.minusSeconds(suppressionSeconds));

        MonitoringEvent event = new MonitoringEvent(device.orElse(null), target.orElse(null), request.source(),
                severity, status, request.eventKey(), request.message(), request.rawMessage(), occurredAt, fingerprint);
        event.setDuplicate(duplicate);
        event.setMetricValue(request.metricValue());
        event.setFacility(request.facility());
        event.setProtocol(request.protocol());
        MonitoringEvent saved = eventRepository.save(event);

        if (request.source() == EventSource.TCP_PING
                && !"TCP_LOCAL_EXECUTION_SAVED".equals(request.protocol())) {
            saveTcpResult(request, device.orElse(null), occurredAt);
        }
        device.ifPresent(value -> updateAlert(value, rule.orElse(null), saved));
        return saved;
    }

    private void updateAlert(Device device, MonitoringRule rule, MonitoringEvent event) {
        Optional<Alert> active = alertRepository.findFirstByDeviceAndAlertKeyAndStatusInOrderByLastOccurredAtDesc(
                device, event.getEventKey(), ACTIVE);
        if (event.getStatus() == AlertStatus.NORMAL || event.getStatus() == AlertStatus.RECOVERED) {
            active.ifPresent(alert -> {
                AlertStatus before = alert.getStatus();
                alert.recover(event.getOccurredAt());
                historyRepository.save(new AlertHistory(alert, before, AlertStatus.RECOVERED, "system",
                        "正常または復旧イベントを検出"));
            });
            device.setStatus(OperationalStatus.NORMAL);
            return;
        }

        Alert alert = active.orElseGet(() -> {
            Alert created = new Alert(device, rule, event.getEventKey(), event.getMessage(), event.getMessage(),
                    event.getSeverity(), event.getStatus(), event.getOccurredAt());
            Alert persisted = alertRepository.save(created);
            historyRepository.save(new AlertHistory(persisted, null, event.getStatus(), "system", "初回障害イベント"));
            return persisted;
        });
        if (active.isPresent()) {
            AlertStatus before = alert.getStatus();
            alert.recordEvent(event.getSeverity(), event.getStatus(), event.getMessage(), event.getOccurredAt());
            if (before != alert.getStatus()) {
                historyRepository.save(new AlertHistory(alert, before, alert.getStatus(), "system", "障害レベル変更"));
            }
        }
        device.setStatus(event.getSeverity() == Severity.CRITICAL
                ? OperationalStatus.CRITICAL : OperationalStatus.WARNING);
        if (!event.isDuplicate()) {
            notificationService.notifyAlert(alert);
        }
    }

    private AlertStatus determineStatus(IngestRequest request, MonitoringRule rule) {
        if (request.status() != null) return request.status();
        if (Boolean.FALSE.equals(request.success())) return AlertStatus.CRITICAL;
        if (Boolean.TRUE.equals(request.success()) && request.metricValue() == null) return AlertStatus.NORMAL;
        if (request.metricValue() != null && rule != null) {
            if (rule.getCriticalThreshold() != null && request.metricValue() >= rule.getCriticalThreshold()) {
                return AlertStatus.CRITICAL;
            }
            if (rule.getWarningThreshold() != null && request.metricValue() >= rule.getWarningThreshold()) {
                return AlertStatus.WARNING;
            }
            return AlertStatus.NORMAL;
        }
        return request.severity() == Severity.CRITICAL ? AlertStatus.CRITICAL
                : request.severity() == Severity.WARNING ? AlertStatus.WARNING : AlertStatus.NORMAL;
    }

    private Severity determineSeverity(IngestRequest request, AlertStatus status) {
        if (request.severity() != null) return request.severity();
        return status == AlertStatus.CRITICAL ? Severity.CRITICAL
                : status == AlertStatus.WARNING ? Severity.WARNING : Severity.INFO;
    }

    private String metricName(IngestRequest request) {
        return request.source() == EventSource.TCP_PING ? "responseMillis" : request.eventKey();
    }

    private void saveTcpResult(IngestRequest request, Device device, Instant occurredAt) {
        boolean success = Boolean.TRUE.equals(request.success());
        long millis = request.metricValue() == null ? 0 : Math.max(0, request.metricValue().longValue());
        String error = success ? null : "CONNECTION_FAILED";
        tcpResultRepository.save(new TcpPingResult(device, request.host(),
                request.port() == null ? 0 : request.port(), success, millis, error,
                success ? null : request.message(), occurredAt,
                request.retryCount() == null ? 0 : request.retryCount()));
    }

    private String fingerprint(IngestRequest request) {
        String input = request.source() + "|" + request.host() + "|" + request.eventKey() + "|" + request.message();
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("JDK 必须提供 SHA-256", ex);
        }
    }
}
