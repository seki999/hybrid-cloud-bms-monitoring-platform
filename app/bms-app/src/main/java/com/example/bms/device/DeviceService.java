package com.example.bms.device;

import com.example.bms.audit.AuditService;
import com.example.bms.common.exception.ResourceNotFoundException;
import com.example.bms.monitoring.MonitoringTarget;
import com.example.bms.monitoring.MonitoringTargetRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 设备登记、检索和详情取得的事务边界。 */
@Service
public class DeviceService {
    private final DeviceRepository repository;
    private final MonitoringTargetRepository targetRepository;
    private final AuditService auditService;

    public DeviceService(DeviceRepository repository, MonitoringTargetRepository targetRepository,
                         AuditService auditService) {
        this.repository = repository;
        this.targetRepository = targetRepository;
        this.auditService = auditService;
    }

    /** 搜索条件、页码和允许列表内的排序字段を组合成分页查询。 */
    @Transactional(readOnly = true)
    public Page<Device> search(String query, int page, int size, String sort, String direction) {
        String safeSort = List.of("name", "hostname", "status", "location").contains(sort) ? sort : "name";
        Sort.Direction safeDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 5), 100),
                Sort.by(safeDirection, safeSort));
        String term = query == null ? "" : query.trim();
        return repository.findByNameContainingIgnoreCaseOrHostnameContainingIgnoreCase(term, term, pageable);
    }

    /** 主键取得设备；不存在时转换为统一 404 业务异常。 */
    @Transactional(readOnly = true)
    public Device get(long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("監視対象機器が見つかりません"));
    }

    /** 详情画面使用的监视目标列表。 */
    @Transactional(readOnly = true)
    public List<MonitoringTarget> targets(Device device) { return targetRepository.findByDeviceOrderByName(device); }

    /** 新建设备并记录审计。 */
    @Transactional
    public Device create(DeviceForm form, String actor) {
        Device device = new Device(form.getName(), form.getHostname(), form.getLocation(), form.getVendor(),
                form.getDeviceType());
        apply(device, form);
        Device saved = repository.save(device);
        auditService.record(actor, "DEVICE_CREATED", "DEVICE", String.valueOf(saved.getId()), saved.getName());
        return saved;
    }

    /** 更新允许的 DTO 字段，避免浏览器修改审计字段或主键。 */
    @Transactional
    public Device update(long id, DeviceForm form, String actor) {
        Device device = get(id);
        apply(device, form);
        auditService.record(actor, "DEVICE_UPDATED", "DEVICE", device.getId().toString(), device.getName());
        return device;
    }

    private void apply(Device device, DeviceForm form) {
        device.setName(form.getName().trim());
        device.setHostname(form.getHostname().trim());
        device.setLocation(form.getLocation().trim());
        device.setVendor(form.getVendor().trim());
        device.setDeviceType(form.getDeviceType());
        device.setStatus(form.getStatus());
        device.setDescription(form.getDescription());
    }
}
