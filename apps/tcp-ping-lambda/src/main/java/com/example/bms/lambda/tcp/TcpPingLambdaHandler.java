package com.example.bms.lambda.tcp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/** AWS Lambda TCP Ping Handler；一回调用可检查多个 host:port。 */
public class TcpPingLambdaHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    private static final Logger log = Logger.getLogger(TcpPingLambdaHandler.class.getName());
    private final SocketProbe probe;
    private final ResultPublisher publisher;

    public TcpPingLambdaHandler() { this(new JavaSocketProbe(), new HttpResultPublisher()); }
    TcpPingLambdaHandler(SocketProbe probe, ResultPublisher publisher) { this.probe = probe; this.publisher = publisher; }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        int timeout = number(input, "timeoutMillis", 2000);
        int retries = number(input, "retries", 2);
        String endpoint = String.valueOf(input.get("resultEndpoint"));
        String apiKey = String.valueOf(input.get("apiKey"));
        List<Map<String, Object>> targets = (List<Map<String, Object>>) input.getOrDefault("targets", List.of());
        List<Map<String, Object>> results = new ArrayList<>();
        for (Map<String, Object> target : targets) {
            String host = String.valueOf(target.get("host"));
            int port = ((Number) target.get("port")).intValue();
            ProbeResult result = probe.connect(host, port, timeout, retries);
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("source", "TCP_PING"); payload.put("host", host); payload.put("eventKey", "tcp-" + port);
            payload.put("message", result.success() ? "TCP connection success" : "TCP connection failed: " + result.errorType());
            payload.put("rawMessage", "host=" + host + " port=" + port + " error=" + result.errorType());
            payload.put("severity", result.success() ? "INFO" : "CRITICAL");
            payload.put("status", result.success() ? "NORMAL" : "CRITICAL");
            payload.put("metricValue", result.elapsedMillis()); payload.put("success", result.success());
            payload.put("port", port); payload.put("retryCount", result.retryCount()); payload.put("protocol", "TCP");
            payload.put("occurredAt", Instant.now().toString());
            publisher.publish(endpoint, apiKey, payload, timeout);
            results.add(payload);
            log.info(() -> "TCP Ping completed host=" + host + " port=" + port + " success=" + result.success());
        }
        return Map.of("checked", results.size(), "results", results);
    }

    private int number(Map<String, Object> input, String key, int fallback) {
        Object value = input.get(key); return value instanceof Number number ? number.intValue() : fallback;
    }

    interface SocketProbe { ProbeResult connect(String host, int port, int timeout, int retries); }
    interface ResultPublisher { void publish(String endpoint, String apiKey, Map<String, Object> payload, int timeout); }
    record ProbeResult(boolean success, long elapsedMillis, String errorType, int retryCount) { }

    static final class JavaSocketProbe implements SocketProbe {
        @Override public ProbeResult connect(String host, int port, int timeout, int retries) {
            long start = System.nanoTime(); String error = null; int attempt = 0;
            for (; attempt <= retries; attempt++) {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(host, port), timeout);
                    return new ProbeResult(true, (System.nanoTime() - start) / 1_000_000, null, attempt);
                } catch (IOException ex) { error = ex.getClass().getSimpleName(); }
            }
            return new ProbeResult(false, (System.nanoTime() - start) / 1_000_000, error, Math.max(0, attempt - 1));
        }
    }

    static final class HttpResultPublisher implements ResultPublisher {
        private final SnmpStylePublisher delegate = new SnmpStylePublisher();
        @Override public void publish(String endpoint, String apiKey, Map<String, Object> payload, int timeout) {
            delegate.publish(endpoint, apiKey, payload, timeout);
        }
    }

    /** 外部依赖を増やさない最小 JSON/HTTP Publisher。 */
    static final class SnmpStylePublisher {
        void publish(String endpoint, String apiKey, Map<String, Object> payload, int timeout) {
            StringBuilder json = new StringBuilder("{");
            payload.forEach((key, value) -> {
                if (json.length() > 1) json.append(',');
                json.append('"').append(key).append("\":");
                if (value instanceof Number || value instanceof Boolean) json.append(value);
                else json.append('"').append(String.valueOf(value).replace("\\", "\\\\").replace("\"", "\\\"")).append('"');
            });
            json.append('}');
            var request = java.net.http.HttpRequest.newBuilder(java.net.URI.create(endpoint))
                    .timeout(java.time.Duration.ofMillis(timeout + 1000L)).header("Content-Type", "application/json")
                    .header("X-BMS-API-Key", apiKey).POST(java.net.http.HttpRequest.BodyPublishers.ofString(json.toString())).build();
            try {
                var response = java.net.http.HttpClient.newHttpClient().send(request, java.net.http.HttpResponse.BodyHandlers.discarding());
                if (response.statusCode() >= 300) throw new IllegalStateException("result API status=" + response.statusCode());
            } catch (IOException ex) { throw new IllegalStateException("result API I/O failure", ex); }
            catch (InterruptedException ex) { Thread.currentThread().interrupt(); throw new IllegalStateException("interrupted", ex); }
        }
    }
}

