package com.example.bms.device;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** 设备主数据访问层；业务判定不得写入 Repository。 */
public interface DeviceRepository extends JpaRepository<Device, Long> {
    Page<Device> findByNameContainingIgnoreCaseOrHostnameContainingIgnoreCase(
            String name, String hostname, Pageable pageable);
    Optional<Device> findFirstByHostnameIgnoreCaseOrNameIgnoreCase(String hostname, String name);
    long countByStatus(OperationalStatus status);
}

