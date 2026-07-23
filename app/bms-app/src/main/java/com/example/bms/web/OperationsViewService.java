package com.example.bms.web;

import com.example.bms.audit.AuditLog;
import com.example.bms.audit.AuditLogRepository;
import com.example.bms.monitoring.TcpPingResult;
import com.example.bms.monitoring.TcpPingResultRepository;
import com.example.bms.notification.NotificationTarget;
import com.example.bms.notification.NotificationTargetRepository;
import com.example.bms.security.AppUser;
import com.example.bms.security.AppUserRepository;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 通知先、TCP结果、审计、用户、JVM 状态画面的只读 Facade。 */
@Service
public class OperationsViewService {
    private final NotificationTargetRepository notifications;
    private final TcpPingResultRepository tcpResults;
    private final AuditLogRepository audits;
    private final AppUserRepository users;

    public OperationsViewService(NotificationTargetRepository notifications, TcpPingResultRepository tcpResults,
                                 AuditLogRepository audits, AppUserRepository users) {
        this.notifications = notifications;
        this.tcpResults = tcpResults;
        this.audits = audits;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<NotificationTarget> notificationTargets() { return notifications.findAll(); }

    @Transactional(readOnly = true)
    public Page<TcpPingResult> tcpPingResults(int page) {
        Page<TcpPingResult> result = tcpResults.findAllByOrderByCheckedAtDesc(PageRequest.of(Math.max(page, 0), 20));
        result.forEach(item -> { if (item.getDevice() != null) item.getDevice().getName(); });
        return result;
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> auditLogs(int page) {
        return audits.findAllByOrderByCreatedAtDesc(PageRequest.of(Math.max(page, 0), 20));
    }

    @Transactional(readOnly = true)
    public List<AppUser> users() { return users.findAllByOrderByUsernameAsc(); }

    /** 只返回非敏感运行信息；环境变量、系统属性完整列表不得展示。 */
    public Map<String, Object> systemStatus() {
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("application", "hybrid-cloud-bms-monitoring-platform");
        status.put("javaVersion", Runtime.version().toString());
        status.put("processors", runtime.availableProcessors());
        status.put("usedMemoryMb", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024);
        status.put("maxMemoryMb", runtime.maxMemory() / 1024 / 1024);
        status.put("uptime", Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()).toString());
        status.put("checkedAt", Instant.now());
        return status;
    }
}

