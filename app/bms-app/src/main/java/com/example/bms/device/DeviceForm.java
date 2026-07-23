package com.example.bms.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** 设备登记画面的输入 DTO；Entity 不直接接收浏览器输入，防止越权字段绑定。 */
public class DeviceForm {
    @NotBlank(message = "機器名を入力してください")
    @Size(max = 80, message = "機器名は80文字以内で入力してください")
    private String name;

    @NotBlank(message = "ホスト名またはIPアドレスを入力してください")
    @Size(max = 255, message = "ホスト名は255文字以内で入力してください")
    @Pattern(regexp = "^[A-Za-z0-9._:-]+$", message = "ホスト名の形式が正しくありません")
    private String hostname;

    @NotBlank(message = "設置場所を入力してください")
    @Size(max = 120, message = "設置場所は120文字以内で入力してください")
    private String location;

    @NotBlank(message = "ベンダーを入力してください")
    @Size(max = 80, message = "ベンダーは80文字以内で入力してください")
    private String vendor;

    @NotNull(message = "機器種別を選択してください")
    private DeviceType deviceType;

    @NotNull(message = "状態を選択してください")
    private OperationalStatus status = OperationalStatus.NORMAL;

    @Size(max = 500, message = "説明は500文字以内で入力してください")
    private String description;

    public static DeviceForm from(Device device) {
        DeviceForm form = new DeviceForm();
        form.name = device.getName();
        form.hostname = device.getHostname();
        form.location = device.getLocation();
        form.vendor = device.getVendor();
        form.deviceType = device.getDeviceType();
        form.status = device.getStatus();
        form.description = device.getDescription();
        return form;
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

