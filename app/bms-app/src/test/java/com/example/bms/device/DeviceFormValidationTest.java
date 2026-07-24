package com.example.bms.device;

import static org.junit.jupiter.api.Assertions.assertTrue;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

/**
 * 验证设备表单的必填项、长度和格式约束，确保无效输入在进入服务层前被拒绝。
 * 测试直接调用 Bean Validation，使失败原因与页面控制器无关且容易定位。
 */
class DeviceFormValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test void rejectsEmptyAndUnsafeHostname() {
        DeviceForm form = new DeviceForm();
        form.setName("");
        form.setHostname("host name<script>");
        form.setLocation("");
        form.setVendor("");
        assertTrue(validator.validate(form).size() >= 5);
    }
}
