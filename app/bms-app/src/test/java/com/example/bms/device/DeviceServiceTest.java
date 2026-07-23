package com.example.bms.device;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.example.bms.audit.AuditService;
import com.example.bms.monitoring.MonitoringTargetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {
    @Mock DeviceRepository repository;
    @Mock MonitoringTargetRepository targets;
    @Mock AuditService audit;
    @InjectMocks DeviceService service;

    @Test void createMapsDtoAndWritesAudit() {
        DeviceForm form = new DeviceForm();
        form.setName(" Router 01 "); form.setHostname("10.0.0.1"); form.setLocation("Tokyo");
        form.setVendor("Vendor"); form.setDeviceType(DeviceType.ROUTER); form.setStatus(OperationalStatus.NORMAL);
        when(repository.save(any(Device.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Device saved = service.create(form, "admin");
        assertEquals("Router 01", saved.getName());
        verify(repository).save(saved);
    }
}

