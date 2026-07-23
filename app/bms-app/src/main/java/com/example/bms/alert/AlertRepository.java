package com.example.bms.alert;

import com.example.bms.common.domain.AlertStatus;
import com.example.bms.device.Device;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/** 持续告警访问层；活跃告警查询由 device + alertKey + 状态索引支持。 */
public interface AlertRepository extends JpaRepository<Alert, Long> {
    Optional<Alert> findFirstByDeviceAndAlertKeyAndStatusInOrderByLastOccurredAtDesc(
            Device device, String alertKey, Collection<AlertStatus> statuses);
    @EntityGraph(attributePaths = {"device", "rule"})
    Page<Alert> findByTitleContainingIgnoreCase(String query, Pageable pageable);
    @EntityGraph(attributePaths = {"device", "rule"})
    List<Alert> findTop8ByOrderByLastOccurredAtDesc();
    long countByStatusIn(Collection<AlertStatus> statuses);
    long countByStatus(AlertStatus status);
}
