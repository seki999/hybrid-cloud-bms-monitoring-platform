package com.example.bms.web;

import com.example.bms.alert.Alert;
import com.example.bms.event.MonitoringEvent;
import java.util.List;

/** Dashboard 模板需要的只读聚合 DTO，避免模板触发额外数据库查询。 */
public record DashboardView(
        long totalDevices,
        long normalDevices,
        long warningDevices,
        long criticalDevices,
        long activeAlerts,
        long syslogCount,
        long snmpTrapCount,
        double tcpPingSuccessRate,
        List<MonitoringEvent> recentEvents,
        List<Alert> recentAlerts,
        List<Long> hourlyTrend) {
}

