package com.example.bms.notification;

import org.springframework.data.jpa.repository.JpaRepository;

/** 通知结果访问层，唯一幂等键在并发重试时提供数据库级最后防线。 */
public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, Long> {
    boolean existsByIdempotencyKey(String idempotencyKey);
}

