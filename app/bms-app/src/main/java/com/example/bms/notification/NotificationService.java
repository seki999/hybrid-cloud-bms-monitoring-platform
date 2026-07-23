package com.example.bms.notification;

import com.example.bms.alert.Alert;
import com.example.bms.audit.AuditService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 告警通知 Adapter；本地发送到 MailHog，生产可替换为 OCI Notifications 等实现。 */
@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationTargetRepository targetRepository;
    private final NotificationDeliveryRepository deliveryRepository;
    private final JavaMailSender mailSender;
    private final AuditService auditService;
    private final boolean enabled;
    private final String from;

    public NotificationService(NotificationTargetRepository targetRepository,
                               NotificationDeliveryRepository deliveryRepository,
                               JavaMailSender mailSender, AuditService auditService,
                               @Value("${bms.notification.enabled:true}") boolean enabled,
                               @Value("${bms.notification.from:bms-monitor@local.invalid}") String from) {
        this.targetRepository = targetRepository;
        this.deliveryRepository = deliveryRepository;
        this.mailSender = mailSender;
        this.auditService = auditService;
        this.enabled = enabled;
        this.from = from;
    }

    /**
     * 级别过滤和幂等键检查后发送通知；单个通知失败不会回滚告警本身。
     *
     * @param alert 已持久化告警
     */
    @Transactional
    public void notifyAlert(Alert alert) {
        List<NotificationTarget> targets = targetRepository.findByEnabledTrueOrderByName();
        for (NotificationTarget target : targets) {
            if (alert.getSeverity().ordinal() < target.getMinimumSeverity().ordinal()) {
                continue;
            }
            String key = alert.getId() + ":" + alert.getEventCount() + ":" + target.getId();
            if (deliveryRepository.existsByIdempotencyKey(key)) {
                continue;
            }
            if (!enabled || !"EMAIL".equals(target.getChannel())) {
                deliveryRepository.save(new NotificationDelivery(alert, key, target.getChannel(), "MOCKED",
                        "安全Mockとして記録。物理設備は操作していません"));
                continue;
            }
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(from);
                message.setTo(target.getAddress());
                message.setSubject("[BMS " + alert.getSeverity() + "] " + alert.getTitle());
                message.setText(alert.getSummary());
                mailSender.send(message);
                deliveryRepository.save(new NotificationDelivery(alert, key, "EMAIL", "SENT", "MailHogへ送信"));
            } catch (RuntimeException ex) {
                log.warn("通知送信に失敗しました alertId={} targetId={} reason={}",
                        alert.getId(), target.getId(), ex.getClass().getSimpleName());
                deliveryRepository.save(new NotificationDelivery(alert, key, "EMAIL", "FAILED",
                        ex.getClass().getSimpleName()));
            }
        }
        auditService.record("system", "NOTIFICATION_PROCESSED", "ALERT", alert.getId().toString(),
                "通知対象の級別・冪等性を確認");
    }
}

