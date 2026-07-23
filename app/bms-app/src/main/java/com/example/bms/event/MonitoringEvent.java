package com.example.bms.event;

import com.example.bms.common.domain.AlertStatus;
import com.example.bms.common.domain.Severity;
import com.example.bms.common.persistence.AuditableEntity;
import com.example.bms.device.Device;
import com.example.bms.monitoring.MonitoringTarget;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * 单次监视观测记录。
 *
 * <p>Event 永远表示一次事实，不因后续恢复或确认而被改写。持续故障由 Alert 聚合多个 Event 表示。</p>
 */
@Entity
@Table(name = "monitoring_events")
public class MonitoringEvent extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private MonitoringTarget target;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private EventSource source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private AlertStatus status;

    @Column(name = "event_key", nullable = false, length = 180)
    private String eventKey;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(name = "raw_message", columnDefinition = "TEXT")
    private String rawMessage;

    @Column(length = 80)
    private String facility;

    @Column(length = 40)
    private String protocol;

    @Column(name = "metric_value")
    private Double metricValue;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(nullable = false)
    private boolean duplicate;

    @Column(nullable = false, length = 64)
    private String fingerprint;

    protected MonitoringEvent() { }

    public MonitoringEvent(Device device, MonitoringTarget target, EventSource source, Severity severity,
                           AlertStatus status, String eventKey, String message, String rawMessage,
                           Instant occurredAt, String fingerprint) {
        this.device = device;
        this.target = target;
        this.source = source;
        this.severity = severity;
        this.status = status;
        this.eventKey = eventKey;
        this.message = message;
        this.rawMessage = rawMessage;
        this.occurredAt = occurredAt;
        this.fingerprint = fingerprint;
    }

    public Device getDevice() { return device; }
    public MonitoringTarget getTarget() { return target; }
    public EventSource getSource() { return source; }
    public Severity getSeverity() { return severity; }
    public AlertStatus getStatus() { return status; }
    public String getEventKey() { return eventKey; }
    public String getMessage() { return message; }
    public String getRawMessage() { return rawMessage; }
    public String getFacility() { return facility; }
    public void setFacility(String facility) { this.facility = facility; }
    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }
    public Double getMetricValue() { return metricValue; }
    public void setMetricValue(Double metricValue) { this.metricValue = metricValue; }
    public Instant getOccurredAt() { return occurredAt; }
    public boolean isDuplicate() { return duplicate; }
    public void setDuplicate(boolean duplicate) { this.duplicate = duplicate; }
    public String getFingerprint() { return fingerprint; }
}

