package com.example.bms.monitoring;

import com.example.bms.common.persistence.AuditableEntity;
import com.example.bms.device.Device;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

/** Java Socket 接続检查的结构化结果，保留错误类别而不是只保存成功/失败。 */
@Entity
@Table(name = "tcp_ping_results")
public class TcpPingResult extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;
    @Column(nullable = false, length = 255)
    private String host;
    @Column(nullable = false)
    private int port;
    @Column(nullable = false)
    private boolean success;
    @Column(name = "response_millis", nullable = false)
    private long responseMillis;
    @Column(name = "error_type", length = 80)
    private String errorType;
    @Column(name = "error_message", length = 500)
    private String errorMessage;
    @Column(name = "checked_at", nullable = false)
    private Instant checkedAt;
    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    protected TcpPingResult() { }
    public TcpPingResult(Device device, String host, int port, boolean success, long responseMillis,
                         String errorType, String errorMessage, Instant checkedAt, int retryCount) {
        this.device = device;
        this.host = host;
        this.port = port;
        this.success = success;
        this.responseMillis = responseMillis;
        this.errorType = errorType;
        this.errorMessage = errorMessage;
        this.checkedAt = checkedAt;
        this.retryCount = retryCount;
    }
    public Device getDevice() { return device; }
    public String getHost() { return host; }
    public int getPort() { return port; }
    public boolean isSuccess() { return success; }
    public long getResponseMillis() { return responseMillis; }
    public String getErrorType() { return errorType; }
    public String getErrorMessage() { return errorMessage; }
    public Instant getCheckedAt() { return checkedAt; }
    public int getRetryCount() { return retryCount; }
}

