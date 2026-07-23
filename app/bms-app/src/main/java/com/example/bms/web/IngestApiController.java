package com.example.bms.web;

import com.example.bms.common.domain.AlertStatus;
import com.example.bms.common.domain.Severity;
import com.example.bms.event.EventProcessingService;
import com.example.bms.event.EventSource;
import com.example.bms.event.IngestRequest;
import com.example.bms.event.MonitoringEvent;
import com.example.bms.infrastructure.DemoDataService;
import com.example.bms.monitoring.TcpPingResult;
import com.example.bms.monitoring.TcpPingService;
import com.example.bms.protocol.snmp.SnmpGetRequest;
import com.example.bms.protocol.snmp.SnmpGetResult;
import com.example.bms.protocol.snmp.SnmpQueryClient;
import com.example.bms.security.ApiKeyService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** AWS Lambda、本地脚本与外部系统使用的 HTTPS/JSON 接收 API。 */
@RestController
@RequestMapping("/api/v1")
@Validated
public class IngestApiController {
    private final ApiKeyService apiKeyService;
    private final EventProcessingService eventProcessor;
    private final DemoDataService demoData;
    private final TcpPingService tcpPingService;
    private final SnmpQueryClient snmpClient;

    public IngestApiController(ApiKeyService apiKeyService, EventProcessingService eventProcessor,
                               DemoDataService demoData, TcpPingService tcpPingService,
                               SnmpQueryClient snmpClient) {
        this.apiKeyService = apiKeyService;
        this.eventProcessor = eventProcessor;
        this.demoData = demoData;
        this.tcpPingService = tcpPingService;
        this.snmpClient = snmpClient;
    }

    @PostMapping("/ingest/events")
    public ResponseEntity<?> ingest(@RequestHeader(value = "X-BMS-API-Key", required = false) String apiKey,
                                    @Valid @RequestBody IngestRequest request) {
        if (!apiKeyService.isValid(apiKey)) return unauthorized();
        MonitoringEvent saved = eventProcessor.process(request);
        return ResponseEntity.accepted().body(Map.of("eventId", saved.getId(), "duplicate", saved.isDuplicate(),
                "status", saved.getStatus().name()));
    }

    @PostMapping("/demo-data/generate")
    public ResponseEntity<?> generate(@RequestHeader(value = "X-BMS-API-Key", required = false) String apiKey) {
        if (!apiKeyService.isValid(apiKey)) return unauthorized();
        return ResponseEntity.ok(demoData.generate());
    }

    @PostMapping("/monitoring/tcp-ping")
    public ResponseEntity<?> tcpPing(@RequestHeader(value = "X-BMS-API-Key", required = false) String apiKey,
                                     @Valid @RequestBody TcpPingCommand command) {
        if (!apiKeyService.isValid(apiKey)) return unauthorized();
        TcpPingResult result = tcpPingService.check(null, command.host(), command.port(),
                command.timeoutMillis(), command.retries());
        eventProcessor.process(new IngestRequest(EventSource.TCP_PING, command.host(),
                "tcp-" + command.port(), result.isSuccess() ? "TCP接続成功" : "TCP接続失敗: " + result.getErrorType(),
                result.getErrorMessage(), result.isSuccess() ? Severity.INFO : Severity.CRITICAL,
                result.isSuccess() ? AlertStatus.NORMAL : AlertStatus.CRITICAL, (double) result.getResponseMillis(),
                result.isSuccess(), command.port(), result.getRetryCount(), null, "TCP_LOCAL_EXECUTION_SAVED",
                result.getCheckedAt()));
        return ResponseEntity.ok(Map.of("success", result.isSuccess(), "responseMillis", result.getResponseMillis(),
                "errorType", result.getErrorType() == null ? "" : result.getErrorType()));
    }

    @PostMapping("/monitoring/snmp-get")
    public ResponseEntity<?> snmpGet(@RequestHeader(value = "X-BMS-API-Key", required = false) String apiKey,
                                     @Valid @RequestBody SnmpGetCommand command) {
        if (!apiKeyService.isValid(apiKey)) return unauthorized();
        SnmpGetResult result = snmpClient.get(new SnmpGetRequest(command.host(), command.port(), command.oid(),
                command.community(), command.timeoutMillis(), command.retries()));
        eventProcessor.process(new IngestRequest(EventSource.SNMP_GET, command.host(), command.oid(),
                result.success() ? "SNMP GET成功: " + result.value() : "SNMP GET失敗: " + result.errorType(),
                "oid=" + result.oid() + " value=" + result.value(),
                result.success() ? Severity.INFO : Severity.CRITICAL,
                result.success() ? AlertStatus.NORMAL : AlertStatus.CRITICAL, null, result.success(), command.port(),
                result.retries(), null, "SNMPv2c", Instant.now()));
        return ResponseEntity.ok(result);
    }

    private ResponseEntity<Map<String, String>> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "invalid API key"));
    }

    /** TCP Socket 检查参数。 */
    public record TcpPingCommand(@NotBlank String host, @Min(1) @Max(65535) int port,
                                 @Min(100) @Max(30000) int timeoutMillis,
                                 @Min(0) @Max(5) int retries) { }

    /** SNMP v2c GET 参数；community 只在请求内存中使用，不写日志。 */
    public record SnmpGetCommand(@NotBlank String host, @Min(1) @Max(65535) int port,
                                 @NotBlank String oid, @NotBlank String community,
                                 @Min(100) @Max(30000) int timeoutMillis,
                                 @Min(0) @Max(5) int retries) { }
}

