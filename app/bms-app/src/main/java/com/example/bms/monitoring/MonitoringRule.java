package com.example.bms.monitoring;

import com.example.bms.common.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * 把标准化后的数值映射为 WARNING/CRITICAL 的规则。
 *
 * <p>阈值保存在规则而非协议 Adapter 中，保证 SNMP GET 和 TCP Ping 可以共用同一告警引擎。</p>
 */
@Entity
@Table(name = "monitoring_rules")
public class MonitoringRule extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_id", nullable = false)
    private MonitoringTarget target;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "metric_name", nullable = false, length = 120)
    private String metricName;

    @Column(name = "warning_threshold")
    private Double warningThreshold;

    @Column(name = "critical_threshold")
    private Double criticalThreshold;

    @Column(name = "suppression_seconds", nullable = false)
    private int suppressionSeconds = 300;

    @Column(nullable = false)
    private boolean enabled = true;

    protected MonitoringRule() { }

    public MonitoringRule(MonitoringTarget target, String name, String metricName,
                          Double warningThreshold, Double criticalThreshold) {
        this.target = target;
        this.name = name;
        this.metricName = metricName;
        this.warningThreshold = warningThreshold;
        this.criticalThreshold = criticalThreshold;
    }

    public MonitoringTarget getTarget() { return target; }
    public String getName() { return name; }
    public String getMetricName() { return metricName; }
    public Double getWarningThreshold() { return warningThreshold; }
    public void setWarningThreshold(Double value) { this.warningThreshold = value; }
    public Double getCriticalThreshold() { return criticalThreshold; }
    public void setCriticalThreshold(Double value) { this.criticalThreshold = value; }
    public int getSuppressionSeconds() { return suppressionSeconds; }
    public void setSuppressionSeconds(int value) { this.suppressionSeconds = value; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}

