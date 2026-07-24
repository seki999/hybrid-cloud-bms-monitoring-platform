package com.example.bms.alert;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.bms.audit.AuditService;
import com.example.bms.common.domain.AlertStatus;
import com.example.bms.common.domain.Severity;
import com.example.bms.device.Device;
import com.example.bms.device.DeviceType;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * 验证告警服务在查询详情时兼容没有监控规则的协议直报告警。
 * 该回归场景可防止可空关联被误当作必填关系并引发页面加载异常。
 */
class AlertServiceTest {

    @Test
    void getAllowsProtocolAlertWithoutMonitoringRule() {
        AlertRepository repository = mock(AlertRepository.class);
        AlertService service = new AlertService(repository, mock(AlertHistoryRepository.class), mock(AuditService.class));
        Device device = new Device("router", "10.0.0.1", "Tokyo", "Vendor", DeviceType.ROUTER);
        Alert alert = new Alert(device, null, "syslog-link-down", "Interface down", "raw protocol alert",
                Severity.WARNING, AlertStatus.WARNING, Instant.now());
        when(repository.findById(1L)).thenReturn(Optional.of(alert));

        assertSame(alert, service.get(1L));
    }
}
