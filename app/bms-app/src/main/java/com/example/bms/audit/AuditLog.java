package com.example.bms.audit;

import com.example.bms.common.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** 安全和运维操作的审计记录；detail 只保存脱敏后的业务摘要。 */
@Entity
@Table(name = "audit_logs")
public class AuditLog extends AuditableEntity {
    @Column(nullable = false, length = 100)
    private String actor;
    @Column(nullable = false, length = 100)
    private String action;
    @Column(name = "resource_type", nullable = false, length = 80)
    private String resourceType;
    @Column(name = "resource_id", length = 80)
    private String resourceId;
    @Column(nullable = false, length = 1000)
    private String detail;

    protected AuditLog() { }
    public AuditLog(String actor, String action, String resourceType, String resourceId, String detail) {
        this.actor = actor;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.detail = detail;
    }
    public String getActor() { return actor; }
    public String getAction() { return action; }
    public String getResourceType() { return resourceType; }
    public String getResourceId() { return resourceId; }
    public String getDetail() { return detail; }
}

