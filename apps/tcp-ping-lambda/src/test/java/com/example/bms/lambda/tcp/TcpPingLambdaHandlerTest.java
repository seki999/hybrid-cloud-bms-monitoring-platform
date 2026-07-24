package com.example.bms.lambda.tcp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * 验证 Lambda 处理器能够解析探测请求，并输出符合 API 契约的成功或失败结果。
 * 测试使用本地可控目标，避免依赖公网连通性和云端 Lambda 运行环境。
 */
class TcpPingLambdaHandlerTest {
    @Test void checksMultipleTargetsAndPublishesEachResult() {
        AtomicInteger published = new AtomicInteger();
        TcpPingLambdaHandler handler = new TcpPingLambdaHandler(
                (host, port, timeout, retries) -> new TcpPingLambdaHandler.ProbeResult(true, 12, null, 0),
                (endpoint, key, payload, timeout) -> published.incrementAndGet());
        Map<String, Object> input = Map.of("resultEndpoint", "https://example.invalid/api", "apiKey", "key",
                "targets", List.of(Map.of("host", "10.0.0.1", "port", 443), Map.of("host", "10.0.0.2", "port", 22)));
        Map<String, Object> result = handler.handleRequest(input, null);
        assertEquals(2, result.get("checked"));
        assertEquals(2, published.get());
    }
}
