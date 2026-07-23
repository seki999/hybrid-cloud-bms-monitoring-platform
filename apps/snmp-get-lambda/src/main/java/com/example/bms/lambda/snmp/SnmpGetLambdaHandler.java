package com.example.bms.lambda.snmp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/**
 * AWS Lambda SNMP GET Handler。
 *
 * <p>Lambda VPC から顧客网络へ到达できる場合に v2c GET を実行し、結果だけを HTTPS API へ転送する。</p>
 */
public class SnmpGetLambdaHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    private static final Logger log = Logger.getLogger(SnmpGetLambdaHandler.class.getName());
    private final SnmpExecutor executor;
    private final ResultPublisher publisher;

    public SnmpGetLambdaHandler() { this(new Snmp4jExecutor(), new HttpsResultPublisher()); }
    SnmpGetLambdaHandler(SnmpExecutor executor, ResultPublisher publisher) {
        this.executor = executor;
        this.publisher = publisher;
    }

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        String host = required(input, "host");
        String oid = required(input, "oid");
        String community = required(input, "community");
        int port = number(input, "port", 161);
        int timeout = number(input, "timeoutMillis", 2000);
        int retries = number(input, "retries", 2);
        String endpoint = required(input, "resultEndpoint");
        String apiKey = required(input, "apiKey");
        long start = System.nanoTime();
        QueryResult result = executor.get(host, port, oid, community, timeout, retries);
        long elapsed = (System.nanoTime() - start) / 1_000_000;

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("source", "SNMP_GET"); payload.put("host", host); payload.put("eventKey", oid);
        payload.put("message", result.success() ? "SNMP GET success: " + result.value() : "SNMP GET failed: " + result.error());
        payload.put("rawMessage", "oid=" + oid + " value=" + result.value());
        payload.put("severity", result.success() ? "INFO" : "CRITICAL");
        payload.put("status", result.success() ? "NORMAL" : "CRITICAL");
        payload.put("success", result.success()); payload.put("port", port); payload.put("retryCount", retries);
        payload.put("protocol", "SNMPv2c"); payload.put("occurredAt", Instant.now().toString());
        publisher.publish(endpoint, apiKey, payload, timeout);
        log.info(() -> "SNMP GET completed host=" + host + " oid=" + oid + " success=" + result.success()
                + " elapsedMs=" + elapsed); // Community/API key は絶対にログへ出さない。
        payload.put("elapsedMillis", elapsed);
        return payload;
    }

    private String required(Map<String, Object> input, String key) {
        Object value = input.get(key);
        if (value == null || value.toString().isBlank()) throw new IllegalArgumentException("missing field: " + key);
        return value.toString();
    }
    private int number(Map<String, Object> input, String key, int fallback) {
        Object value = input.get(key); return value instanceof Number number ? number.intValue() : fallback;
    }

    interface SnmpExecutor { QueryResult get(String host, int port, String oid, String community, int timeout, int retries); }
    interface ResultPublisher { void publish(String endpoint, String apiKey, Map<String, Object> payload, int timeout); }
    record QueryResult(boolean success, String value, String error) { }

    static final class Snmp4jExecutor implements SnmpExecutor {
        @Override public QueryResult get(String host, int port, String oid, String community, int timeout, int retries) {
            try (DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping(); Snmp snmp = new Snmp(transport)) {
                transport.listen();
                CommunityTarget<UdpAddress> target = new CommunityTarget<>();
                target.setAddress(new UdpAddress(host + "/" + port)); target.setCommunity(new OctetString(community));
                target.setVersion(SnmpConstants.version2c); target.setTimeout(timeout); target.setRetries(retries);
                PDU pdu = new PDU(); pdu.setType(PDU.GET); pdu.add(new VariableBinding(new OID(oid)));
                ResponseEvent<UdpAddress> response = snmp.send(pdu, target);
                if (response == null || response.getResponse() == null) return new QueryResult(false, null, "TIMEOUT");
                if (response.getResponse().getErrorStatus() != PDU.noError) {
                    return new QueryResult(false, null, response.getResponse().getErrorStatusText());
                }
                return new QueryResult(true, response.getResponse().get(0).getVariable().toString(), null);
            } catch (IOException | IllegalArgumentException ex) {
                return new QueryResult(false, null, ex.getClass().getSimpleName());
            }
        }
    }

    static final class HttpsResultPublisher implements ResultPublisher {
        @Override public void publish(String endpoint, String apiKey, Map<String, Object> payload, int timeout) {
            String json = toJson(payload);
            HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint)).timeout(Duration.ofMillis(timeout + 1000L))
                    .header("Content-Type", "application/json").header("X-BMS-API-Key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json)).build();
            try {
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() >= 300) throw new IllegalStateException("result API status=" + response.statusCode());
            } catch (IOException ex) {
                throw new IllegalStateException("result API I/O failure", ex);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt(); throw new IllegalStateException("result API interrupted", ex);
            }
        }
        private static String toJson(Map<String, Object> values) {
            StringBuilder json = new StringBuilder("{");
            values.forEach((key, value) -> {
                if (json.length() > 1) json.append(',');
                json.append('"').append(key).append("\":");
                if (value instanceof Number || value instanceof Boolean) json.append(value);
                else json.append('"').append(String.valueOf(value).replace("\\", "\\\\").replace("\"", "\\\"")).append('"');
            });
            return json.append('}').toString();
        }
    }
}

