package com.example.bms.monitoring;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import com.example.bms.audit.AuditService;
import org.junit.jupiter.api.Test;

class AlertCheckSchedulerTest {
    @Test void recordsLastRunForHealthIndicator() {
        AlertCheckScheduler scheduler = new AlertCheckScheduler(mock(AuditService.class), true);
        scheduler.checkAlerts();
        assertNotNull(scheduler.getLastRun());
    }
}
