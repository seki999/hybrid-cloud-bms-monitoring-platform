package com.example.bms.lambda.snmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

/**
 * 验证 SNMP Get Lambda 的输入校验、客户端调用和响应序列化边界。
 * 外部设备访问被隔离，使测试只关注函数处理器的稳定契约。
 */
class SnmpGetLambdaHandlerTest {
    @Test void forwardsSuccessfulResultWithoutRealNetwork() {
        AtomicBoolean published = new AtomicBoolean();
        SnmpGetLambdaHandler handler = new SnmpGetLambdaHandler(
                (host, port, oid, community, timeout, retries) -> new SnmpGetLambdaHandler.QueryResult(true, "42", null),
                (endpoint, key, payload, timeout) -> published.set(true));
        Map<String, Object> input = new HashMap<>(Map.of("host", "10.0.0.1", "oid", "1.3.6.1.2.1.1.3.0",
                "community", "secret-from-env", "resultEndpoint", "https://example.invalid/api", "apiKey", "key"));
        Map<String, Object> result = handler.handleRequest(input, null);
        assertEquals(true, result.get("success"));
        assertTrue(published.get());
    }
}
