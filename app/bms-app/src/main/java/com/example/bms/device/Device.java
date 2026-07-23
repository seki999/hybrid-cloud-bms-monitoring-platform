package com.example.bms.device;

import com.example.bms.common.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * 客户侧设备或虚拟通信服务的主数据。
 *
 * <p>协议事件通过 hostname/IP 解析到本实体；把主数据与 Event 分离可确保设备改名不会修改历史原文。</p>
 */
@Entity
@Table(name = "devices")
public class Device extends AuditableEntity {

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String hostname;

    @Column(nullable = false, length = 120)
    private String location;

    @Column(nullable = false, length = 80)
    private String vendor;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false, length = 32)
    private DeviceType deviceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private OperationalStatus status = OperationalStatus.NORMAL;

    @Column(length = 500)
    private String description;

    protected Device() { }

    public Device(String name, String hostname, String location, String vendor, DeviceType deviceType) {
        this.name = name;
        this.hostname = hostname;
        this.location = location;
        this.vendor = vendor;
        this.deviceType = deviceType;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public DeviceType getDeviceType() { return deviceType; }
    public void setDeviceType(DeviceType deviceType) { this.deviceType = deviceType; }
    public OperationalStatus getStatus() { return status; }
    public void setStatus(OperationalStatus status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

