package com.example.bms.monitoring;

import com.example.bms.device.Device;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** 监视目标访问层。 */
public interface MonitoringTargetRepository extends JpaRepository<MonitoringTarget, Long> {
    List<MonitoringTarget> findByDeviceOrderByName(Device device);
    List<MonitoringTarget> findByTargetTypeAndEnabledTrue(TargetType targetType);
    Optional<MonitoringTarget> findFirstByDeviceAndTargetTypeAndEnabledTrue(Device device, TargetType targetType);
}
