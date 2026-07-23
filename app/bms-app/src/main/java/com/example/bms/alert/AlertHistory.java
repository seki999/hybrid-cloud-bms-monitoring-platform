package com.example.bms.alert;

import com.example.bms.common.domain.AlertStatus;
import com.example.bms.common.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** 告警状态迁移的不可变履历，用于回答谁在何时进行了什么操作。 */
@Entity
@Table(name = "alert_history")
public class AlertHistory extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alert_id", nullable = false)
    private Alert alert;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 24)
    private AlertStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 24)
    private AlertStatus toStatus;

    @Column(nullable = false, length = 100)
    private String actor;

    @Column(nullable = false, length = 500)
    private String reason;

    protected AlertHistory() { }

    public AlertHistory(Alert alert, AlertStatus fromStatus, AlertStatus toStatus, String actor, String reason) {
        this.alert = alert;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.actor = actor;
        this.reason = reason;
    }

    public Alert getAlert() { return alert; }
    public AlertStatus getFromStatus() { return fromStatus; }
    public AlertStatus getToStatus() { return toStatus; }
    public String getActor() { return actor; }
    public String getReason() { return reason; }
}

