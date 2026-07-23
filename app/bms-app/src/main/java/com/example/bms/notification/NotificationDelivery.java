package com.example.bms.notification;

import com.example.bms.alert.Alert;
import com.example.bms.common.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** 每次通知尝试的保存结果；幂等键阻止 Function 或 Worker 重试时重复发送。 */
@Entity
@Table(name = "notification_deliveries")
public class NotificationDelivery extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alert_id", nullable = false)
    private Alert alert;
    @Column(name = "idempotency_key", nullable = false, unique = true, length = 180)
    private String idempotencyKey;
    @Column(nullable = false, length = 40)
    private String channel;
    @Column(nullable = false, length = 24)
    private String status;
    @Column(length = 500)
    private String detail;

    protected NotificationDelivery() { }
    public NotificationDelivery(Alert alert, String idempotencyKey, String channel, String status, String detail) {
        this.alert = alert;
        this.idempotencyKey = idempotencyKey;
        this.channel = channel;
        this.status = status;
        this.detail = detail;
    }
    public Alert getAlert() { return alert; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getChannel() { return channel; }
    public String getStatus() { return status; }
    public String getDetail() { return detail; }
}

