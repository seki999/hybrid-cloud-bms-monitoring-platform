package com.example.bms.notification;

import com.example.bms.common.domain.Severity;
import com.example.bms.common.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/** 告警通知先；地址可指向邮件，生产环境由 Adapter 替换为 OCI Notifications 等服务。 */
@Entity
@Table(name = "notification_targets")
public class NotificationTarget extends AuditableEntity {
    @Column(nullable = false, length = 120)
    private String name;
    @Column(nullable = false, length = 40)
    private String channel;
    @Column(nullable = false, length = 255)
    private String address;
    @Enumerated(EnumType.STRING)
    @Column(name = "minimum_severity", nullable = false, length = 16)
    private Severity minimumSeverity = Severity.WARNING;
    @Column(nullable = false)
    private boolean enabled = true;

    protected NotificationTarget() { }
    public NotificationTarget(String name, String channel, String address, Severity minimumSeverity) {
        this.name = name;
        this.channel = channel;
        this.address = address;
        this.minimumSeverity = minimumSeverity;
    }
    public String getName() { return name; }
    public String getChannel() { return channel; }
    public String getAddress() { return address; }
    public Severity getMinimumSeverity() { return minimumSeverity; }
    public boolean isEnabled() { return enabled; }
}

