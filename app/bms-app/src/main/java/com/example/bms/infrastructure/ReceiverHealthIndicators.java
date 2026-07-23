package com.example.bms.infrastructure;

import com.example.bms.monitoring.AlertCheckScheduler;
import com.example.bms.protocol.snmp.SnmpTrapReceiver;
import com.example.bms.protocol.syslog.SyslogReceiver;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 协议接收器与 Worker 的自定义 Health Indicator。数据库 Health 由 Actuator 自动提供。 */
@Configuration
public class ReceiverHealthIndicators {
    @Bean
    HealthIndicator syslogReceiverHealth(SyslogReceiver receiver) {
        return () -> !receiver.isEnabled() ? Health.up().withDetail("state", "disabled-for-component").build()
                : receiver.isRunning() ? Health.up().withDetail("port", receiver.getPort()).build()
                : Health.down().withDetail("reason", "receiver stopped").build();
    }

    @Bean
    HealthIndicator snmpReceiverHealth(SnmpTrapReceiver receiver) {
        return () -> !receiver.isEnabled() ? Health.up().withDetail("state", "disabled-for-component").build()
                : receiver.isRunning() ? Health.up().withDetail("port", receiver.getPort()).build()
                : Health.down().withDetail("reason", "receiver stopped").build();
    }

    @Bean
    HealthIndicator alertWorkerHealth(AlertCheckScheduler scheduler) {
        return () -> Health.up().withDetail("state", scheduler.isEnabled() ? "enabled" : "disabled-for-component")
                .withDetail("lastRun", String.valueOf(scheduler.getLastRun())).build();
    }
}
