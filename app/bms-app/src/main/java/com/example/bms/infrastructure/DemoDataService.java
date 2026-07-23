package com.example.bms.infrastructure;

import com.example.bms.alert.Alert;
import com.example.bms.alert.AlertRepository;
import com.example.bms.alert.AlertService;
import com.example.bms.common.domain.AlertStatus;
import com.example.bms.common.domain.Severity;
import com.example.bms.event.EventProcessingService;
import com.example.bms.event.EventSource;
import com.example.bms.event.IngestRequest;
import com.example.bms.event.MonitoringEventRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

/**
 * 一键生成可重复演示数据。
 *
 * <p>主数据由 Flyway 创建，本服务只补足各协议最低事件数。再次执行时按当前计数补差额，不会无限复制。</p>
 */
@Service
public class DemoDataService implements ApplicationRunner {
    private final MonitoringEventRepository events;
    private final AlertRepository alerts;
    private final EventProcessingService processor;
    private final AlertService alertService;
    private final boolean initialize;

    public DemoDataService(MonitoringEventRepository events, AlertRepository alerts,
                           EventProcessingService processor, AlertService alertService,
                           @Value("${bms.demo-data.initialize:false}") boolean initialize) {
        this.events = events;
        this.alerts = alerts;
        this.processor = processor;
        this.alertService = alertService;
        this.initialize = initialize;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (initialize) generate();
    }

    /** 生成 30 Syslog、20 Trap、20 GET、20 TCP Ping 以及各类告警状态。 */
    public synchronized DemoDataSummary generate() {
        generateSyslog();
        generateSnmpTrap();
        generateSnmpGet();
        generateTcpPing();
        ensureLifecycleExamples();
        return new DemoDataSummary(events.countBySource(EventSource.SYSLOG),
                events.countBySource(EventSource.SNMP_TRAP), events.countBySource(EventSource.SNMP_GET),
                events.countBySource(EventSource.TCP_PING), alerts.count());
    }

    private void generateSyslog() {
        long current = events.countBySource(EventSource.SYSLOG);
        for (long i = current; i < 30; i++) {
            boolean critical = i % 11 == 0;
            boolean warning = !critical && i % 6 == 0;
            processor.process(new IngestRequest(EventSource.SYSLOG, "10.20.1.11",
                    "interface-ge-0/0/" + (i % 4),
                    critical ? "インターフェース断を検出" : warning ? "パケット廃棄率が上昇" : "定期状態通知を受信",
                    "<" + (critical ? 2 : warning ? 4 : 14) + ">Jul 23 10:2" + (i % 10)
                            + ":00 edge-router-01 BMS event sequence=" + i,
                    critical ? Severity.CRITICAL : warning ? Severity.WARNING : Severity.INFO,
                    critical ? AlertStatus.CRITICAL : warning ? AlertStatus.WARNING : AlertStatus.NORMAL,
                    null, null, null, 0, "local0", "RFC3164",
                    Instant.now().minus(30 - i, ChronoUnit.MINUTES)));
        }
    }

    private void generateSnmpTrap() {
        long current = events.countBySource(EventSource.SNMP_TRAP);
        for (long i = current; i < 20; i++) {
            boolean failure = i % 5 == 0;
            processor.process(new IngestRequest(EventSource.SNMP_TRAP, "10.30.1.11",
                    "linkDown.1.3.6.1.2.1.2.2.1.8." + (i % 3),
                    failure ? "SNMP Trap: linkDown" : "SNMP Trap: linkUp",
                    "1.3.6.1.6.3.1.1.5." + (failure ? "3" : "4") + " ifIndex=" + (i % 3 + 1),
                    failure ? Severity.CRITICAL : Severity.INFO,
                    failure ? AlertStatus.CRITICAL : AlertStatus.RECOVERED,
                    (double) (failure ? 2 : 1), null, 162, 0, null, "SNMPv2c",
                    Instant.now().minus(20 - i, ChronoUnit.MINUTES)));
        }
    }

    private void generateSnmpGet() {
        long current = events.countBySource(EventSource.SNMP_GET);
        for (long i = current; i < 20; i++) {
            double utilization = 20 + (i * 7 % 79);
            Severity severity = utilization >= 90 ? Severity.CRITICAL
                    : utilization >= 75 ? Severity.WARNING : Severity.INFO;
            processor.process(new IngestRequest(EventSource.SNMP_GET, "10.20.1.12",
                    "1.3.6.1.4.1.2021.10.1.3.1", "CPU使用率 " + utilization + "%",
                    "OID=1.3.6.1.4.1.2021.10.1.3.1 value=" + utilization,
                    severity, severity == Severity.CRITICAL ? AlertStatus.CRITICAL
                            : severity == Severity.WARNING ? AlertStatus.WARNING : AlertStatus.NORMAL,
                    utilization, true, 161, 1, null, "SNMPv2c",
                    Instant.now().minus(40 - i, ChronoUnit.MINUTES)));
        }
    }

    private void generateTcpPing() {
        long current = events.countBySource(EventSource.TCP_PING);
        for (long i = current; i < 20; i++) {
            boolean success = i % 6 != 0;
            double elapsed = success ? 18 + (i * 13 % 280) : 1500;
            processor.process(new IngestRequest(EventSource.TCP_PING, "api.service.local", "tcp-443",
                    success ? "HTTPSポート接続成功" : "HTTPSポート接続タイムアウト",
                    "host=api.service.local port=443 elapsedMs=" + elapsed,
                    success ? Severity.INFO : Severity.CRITICAL,
                    success ? AlertStatus.NORMAL : AlertStatus.CRITICAL,
                    elapsed, success, 443, success ? 0 : 2, null, "TCP",
                    Instant.now().minus(25 - i, ChronoUnit.MINUTES)));
        }
    }

    private void ensureLifecycleExamples() {
        List<AlertStatus> active = List.of(AlertStatus.WARNING, AlertStatus.CRITICAL, AlertStatus.ACKNOWLEDGED);
        if (alerts.countByStatusIn(active) < 3) {
            for (int i = 0; i < 4; i++) {
                processor.process(new IngestRequest(EventSource.SYSLOG, "10.20.1.12", "demo-active-" + i,
                        i % 2 == 0 ? "VPNトンネル断を継続検出" : "BGPピア状態が不安定",
                        "<2>Jul 23 10:40:00 edge-router-02 active-alert=" + i,
                        i % 2 == 0 ? Severity.CRITICAL : Severity.WARNING,
                        i % 2 == 0 ? AlertStatus.CRITICAL : AlertStatus.WARNING,
                        null, null, null, 0, "daemon", "RFC3164", Instant.now()));
            }
        }
        List<Alert> all = alerts.findAll();
        boolean hasAcknowledged = all.stream().anyMatch(a -> a.getStatus() == AlertStatus.ACKNOWLEDGED);
        boolean hasClosed = all.stream().anyMatch(a -> a.getStatus() == AlertStatus.CLOSED);
        if (!hasAcknowledged && !all.isEmpty()) alertService.acknowledge(all.get(0).getId(), "operator");
        if (!hasClosed && all.size() > 1) alertService.close(all.get(1).getId(), "admin");
    }

    /** 生成脚本和 API 返回的计数摘要。 */
    public record DemoDataSummary(long syslog, long snmpTrap, long snmpGet, long tcpPing, long alerts) { }
}

