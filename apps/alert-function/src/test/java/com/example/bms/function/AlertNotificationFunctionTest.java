package com.example.bms.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * 验证告警通知函数对事件负载的解析以及邮件/回调输出内容。
 * 测试固定输入数据，确保无服务器运行时升级不会改变通知契约。
 */
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
