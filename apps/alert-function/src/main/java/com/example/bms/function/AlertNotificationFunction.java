package com.example.bms.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * OCI Functions 风格的告警通知函数。
 *
 * <p>CRITICAL 选择邮件与安全 Mock alarm，WARNING 只发邮件；幂等键、重试和审计均在函数边界实现。</p>
 */
public class AlertNotificationFunction {
    private static final Logger log = Logger.getLogger(AlertNotificationFunction.class.getName());
    private static final Set<String> DELIVERED = ConcurrentHashMap.newKeySet();
    private final NotificationGateway gateway;

    public AlertNotificationFunction() { this((channel, payload) -> true); }
    AlertNotificationFunction(NotificationGateway gateway) { this.gateway = gateway; }

    /**
     * Fn Project 可直接把 JSON 反序列化为 Map 并调用此方法。
     *
     * @param payload 包含 alertId、severity、title、recipient、idempotencyKey
     * @return 每个通道的保存结果
     */
    public Map<String, Object> handleRequest(Map<String, Object> payload) {
        String severity = required(payload, "severity");
        String key = required(payload, "idempotencyKey");
        if (!DELIVERED.add(key)) {
            log.info(() -> "duplicate notification suppressed key=" + key);
            return Map.of("status", "DUPLICATE_SUPPRESSED", "idempotencyKey", key, "deliveries", List.of());
        }
        List<String> channels = severity.equals("CRITICAL") ? List.of("EMAIL", "MOCK_ALARM")
                : severity.equals("WARNING") ? List.of("EMAIL") : List.of("AUDIT_ONLY");
        List<Map<String, Object>> results = new ArrayList<>();
        boolean allSucceeded = true;
        for (String channel : channels) {
            boolean sent = false; int attempts = 0;
            for (; attempts < 3 && !sent; attempts++) sent = gateway.send(channel, payload);
            results.add(Map.of("channel", channel, "status", sent ? "SENT" : "FAILED", "attempts", attempts));
            allSucceeded &= sent;
        }
        if (!allSucceeded) DELIVERED.remove(key); // 全通道未完成时允许 OCI Functions 重试。
        boolean completed = allSucceeded;
        log.info(() -> "notification audit alertId=" + payload.get("alertId") + " severity=" + severity
                + " success=" + completed);
        return Map.of("status", completed ? "COMPLETED" : "RETRYABLE_FAILURE",
                "idempotencyKey", key, "deliveries", results);
    }

    private String required(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null || value.toString().isBlank()) throw new IllegalArgumentException("missing field: " + key);
        return value.toString();
    }

    @FunctionalInterface
    interface NotificationGateway { boolean send(String channel, Map<String, Object> payload); }
}
