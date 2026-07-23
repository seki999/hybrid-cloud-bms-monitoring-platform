package com.example.bms.monitoring;

import com.example.bms.common.persistence.AuditableEntity;
import com.example.bms.device.Device;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * 可执行的监视目标。
 *
 * <p>host/port/OID 与设备主数据分离，使一台路由器可以同时接收 Syslog、Trap 并执行多个 GET/Ping。</p>
 */
@Entity
@Table(name = "monitoring_targets")
public class MonitoringTarget extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 24)
    private TargetType targetType;

    @Column(nullable = false, length = 255)
    private String host;

    private Integer port;

    @Column(length = 255)
    private String oid;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "interval_seconds", nullable = false)
    private int intervalSeconds = 60;

    protected MonitoringTarget() { }

    public MonitoringTarget(Device device, String name, TargetType targetType, String host, Integer port) {
        this.device = device;
        this.name = name;
        this.targetType = targetType;
        this.host = host;
        this.port = port;
    }

    public Device getDevice() { return device; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public TargetType getTargetType() { return targetType; }
    public void setTargetType(TargetType targetType) { this.targetType = targetType; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }
    public String getOid() { return oid; }
    public void setOid(String oid) { this.oid = oid; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getIntervalSeconds() { return intervalSeconds; }
    public void setIntervalSeconds(int intervalSeconds) { this.intervalSeconds = intervalSeconds; }
}

