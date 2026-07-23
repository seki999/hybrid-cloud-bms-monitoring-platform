package com.example.bms.notification;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** 通知先访问层。 */
public interface NotificationTargetRepository extends JpaRepository<NotificationTarget, Long> {
    List<NotificationTarget> findByEnabledTrueOrderByName();
}

