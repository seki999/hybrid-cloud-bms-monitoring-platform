package com.example.bms.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class AlertNotificationFunctionTest {
    @Test void criticalUsesTwoChannelsAndSuppressesDuplicate() {
        AtomicInteger sends = new AtomicInteger();
        AlertNotificationFunction function = new AlertNotificationFunction((channel, payload) -> { sends.incrementAndGet(); return true; });
        Map<String, Object> payload = Map.of("alertId", 101, "severity", "CRITICAL", "title", "VPN down",
                "idempotencyKey", "test-critical-101");
        assertEquals("COMPLETED", function.handleRequest(payload).get("status"));
        assertEquals(2, sends.get());
        assertEquals("DUPLICATE_SUPPRESSED", function.handleRequest(payload).get("status"));
        assertEquals(2, sends.get());
    }
}

