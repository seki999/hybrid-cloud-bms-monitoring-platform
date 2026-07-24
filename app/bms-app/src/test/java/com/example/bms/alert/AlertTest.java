package com.example.bms.alert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.example.bms.common.domain.AlertStatus;
import com.example.bms.common.domain.Severity;
import com.example.bms.device.Device;
import com.example.bms.device.DeviceType;
import java.time.Instant;
import org.junit.jupiter.api.Test;

/**
 * 验证告警聚合根的状态迁移、重复事件计数和时间字段更新规则。
 * 领域对象测试不启动 Spring，以精确保护核心业务不变量。
 */
class AlertTest {
    @Test void lifecycleKeepsEventCountAndOperator() {
        Device device = new Device("router", "10.0.0.1", "Tokyo", "Vendor", DeviceType.ROUTER);
        Alert alert = new Alert(device, null, "if-down", "Interface down", "first",
                Severity.WARNING, AlertStatus.WARNING, Instant.now());
        alert.recordEvent(Severity.CRITICAL, AlertStatus.CRITICAL, "escalated", Instant.now());
        assertEquals(2, alert.getEventCount());
        assertEquals(Severity.CRITICAL, alert.getSeverity());
        alert.acknowledge("operator", Instant.now());
        assertEquals(AlertStatus.ACKNOWLEDGED, alert.getStatus());
        assertEquals("operator", alert.getAcknowledgedBy());
        alert.recover(Instant.now());
        assertEquals(AlertStatus.RECOVERED, alert.getStatus());
        alert.close(Instant.now());
        assertEquals(AlertStatus.CLOSED, alert.getStatus());
        assertNotNull(alert.getClosedAt());
    }
}
