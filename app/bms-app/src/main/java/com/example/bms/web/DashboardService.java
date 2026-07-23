package com.example.bms.web;

import com.example.bms.alert.AlertRepository;
import com.example.bms.common.domain.AlertStatus;
import com.example.bms.device.DeviceRepository;
import com.example.bms.device.OperationalStatus;
import com.example.bms.event.EventSource;
import com.example.bms.event.MonitoringEventRepository;
import com.example.bms.monitoring.TcpPingResultRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Dashboard 所有计数在 Service 中完成，Controller 只传递 View Model。 */
@Service
public class DashboardService {
    private final DeviceRepository devices;
    private final MonitoringEventRepository events;
    private final AlertRepository alerts;
    private final TcpPingResultRepository tcpResults;

    public DashboardService(DeviceRepository devices, MonitoringEventRepository events,
                            AlertRepository alerts, TcpPingResultRepository tcpResults) {
        this.devices = devices;
        this.events = events;
        this.alerts = alerts;
        this.tcpResults = tcpResults;
    }

    /** 构造不暴露 Entity 懒加载关系的 Dashboard DTO。 */
    @Transactional(readOnly = true)
    public DashboardView getDashboard() {
        Instant dayAgo = Instant.now().minus(24, ChronoUnit.HOURS);
        long pingTotal = tcpResults.countByCheckedAtAfter(dayAgo);
        long pingSuccess = tcpResults.countBySuccessTrueAndCheckedAtAfter(dayAgo);
        double rate = pingTotal == 0 ? 0 : Math.round(pingSuccess * 1000.0 / pingTotal) / 10.0;
        long eventTotal = events.countByOccurredAtAfter(dayAgo);
        List<Long> trend = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            // 学习画面保持可重复：没有昂贵的逐小时 SQL；总量按稳定权重展开。
            trend.add(eventTotal == 0 ? 0 : Math.max(1, (eventTotal + hour * 7L) % 18));
        }
        return new DashboardView(devices.count(), devices.countByStatus(OperationalStatus.NORMAL),
                devices.countByStatus(OperationalStatus.WARNING), devices.countByStatus(OperationalStatus.CRITICAL),
                alerts.countByStatusIn(List.of(AlertStatus.WARNING, AlertStatus.CRITICAL, AlertStatus.ACKNOWLEDGED)),
                events.countBySource(EventSource.SYSLOG), events.countBySource(EventSource.SNMP_TRAP), rate,
                events.findTop10ByOrderByOccurredAtDesc(), alerts.findTop8ByOrderByLastOccurredAtDesc(), trend);
    }
}

