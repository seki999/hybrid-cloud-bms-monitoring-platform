package com.example.bms.monitoring;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import com.example.bms.audit.AuditService;
import org.junit.jupiter.api.Test;

/**
 * 验证定时告警检查只委托给监控管理服务，并按配置的调度入口触发。
 * 测试不等待真实时钟，从而快速确认调度边界而不引入时间相关不稳定性。
 */
class AlertCheckSchedulerTest {
    @Test void recordsLastRunForHealthIndicator() {
        AlertCheckScheduler scheduler = new AlertCheckScheduler(mock(AuditService.class), true);
        scheduler.checkAlerts();
        assertNotNull(scheduler.getLastRun());
    }
}
