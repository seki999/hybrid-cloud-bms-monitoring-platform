package com.example.bms.alert;

import com.example.bms.common.domain.AlertStatus;
import com.example.bms.common.domain.Severity;
import com.example.bms.common.persistence.AuditableEntity;
import com.example.bms.device.Device;
import com.example.bms.monitoring.MonitoringRule;
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
 * 由一个或多个 Event 形成的持续故障。
 *
 * <p>相同 device + alertKey 的未关闭记录只保留一条，重复事件只累加计数并更新时间，避免告警风暴。</p>
 */
@Entity
@Table(name = "alerts")
public class Alert extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    private MonitoringRule rule;

    @Column(name = "alert_key", nullable = false, length = 180)
    private String alertKey;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 1000)
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private AlertStatus status;

    @Column(name = "first_occurred_at", nullable = false)
    private Instant firstOccurredAt;

    @Column(name = "last_occurred_at", nullable = false)
    private Instant lastOccurredAt;

    @Column(name = "event_count", nullable = false)
    private int eventCount;

    @Column(name = "acknowledged_by", length = 100)
    private String acknowledgedBy;

    @Column(name = "acknowledged_at")
    private Instant acknowledgedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    protected Alert() { }

    public Alert(Device device, MonitoringRule rule, String alertKey, String title, String summary,
                 Severity severity, AlertStatus status, Instant occurredAt) {
        this.device = device;
        this.rule = rule;
        this.alertKey = alertKey;
        this.title = title;
        this.summary = summary;
        this.severity = severity;
        this.status = status;
        this.firstOccurredAt = occurredAt;
        this.lastOccurredAt = occurredAt;
        this.eventCount = 1;
    }

    /** 重复或升级 Event 到达时更新持续告警，而不创建新 Alert。 */
    public void recordEvent(Severity newSeverity, AlertStatus newStatus, String newSummary, Instant occurredAt) {
        eventCount++;
        lastOccurredAt = occurredAt;
        summary = newSummary;
        if (newSeverity.ordinal() >= severity.ordinal()) {
            severity = newSeverity;
        }
        status = newStatus;
    }

    /** 操作员确认后保留故障但记录责任人和时间。 */
    public void acknowledge(String username, Instant now) {
        acknowledgedBy = username;
        acknowledgedAt = now;
        status = AlertStatus.ACKNOWLEDGED;
    }

    /** 恢复 Event 将持续故障标记为 RECOVERED，历史仍可追踪。 */
    public void recover(Instant now) {
        status = AlertStatus.RECOVERED;
        lastOccurredAt = now;
    }

    /** 人工关闭是生命周期终点；保留数据库记录用于报表与审计。 */
    public void close(Instant now) {
        status = AlertStatus.CLOSED;
        closedAt = now;
    }

    public Device getDevice() { return device; }
    public MonitoringRule getRule() { return rule; }
    public String getAlertKey() { return alertKey; }
    public String getTitle() { return title; }
    public String getSummary() { return summary; }
    public Severity getSeverity() { return severity; }
    public AlertStatus getStatus() { return status; }
    public Instant getFirstOccurredAt() { return firstOccurredAt; }
    public Instant getLastOccurredAt() { return lastOccurredAt; }
    public int getEventCount() { return eventCount; }
    public String getAcknowledgedBy() { return acknowledgedBy; }
    public Instant getAcknowledgedAt() { return acknowledgedAt; }
    public Instant getClosedAt() { return closedAt; }
}

