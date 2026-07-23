package com.example.bms.alert;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** 告警生命周期履历访问层。 */
public interface AlertHistoryRepository extends JpaRepository<AlertHistory, Long> {
    List<AlertHistory> findByAlertOrderByCreatedAtDesc(Alert alert);
}

