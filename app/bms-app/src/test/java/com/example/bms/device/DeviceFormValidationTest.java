package com.example.bms.device;

import static org.junit.jupiter.api.Assertions.assertTrue;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

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

