package com.example.bms.lambda.snmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

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

