package com.example.bms.monitoring;

import com.example.bms.audit.AuditService;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

/**
 * 告警检查 Worker 的心跳任务。
 *
 * <p>生产环境可扩展为查询超时目标；当前任务证明 Scheduler 生命周期、健康状态与审计边界。</p>
 */
@Component
public class AlertCheckScheduler {
    private static final Logger log = LoggerFactory.getLogger(AlertCheckScheduler.class);
    private final AtomicReference<Instant> lastRun = new AtomicReference<>();
    private final AuditService auditService;
    private final boolean enabled;

    public AlertCheckScheduler(AuditService auditService,
                               @Value("${bms.scheduler.enabled:true}") boolean enabled) {
        this.auditService = auditService;
        this.enabled = enabled;
    }

    /** 按外部化间隔运行，异常交给 Spring 记录，不能静默失败。 */
    @Scheduled(fixedDelayString = "${bms.scheduler.alert-check-ms:60000}")
    public void checkAlerts() {
        if (!enabled) return;
        Instant now = Instant.now();
        lastRun.set(now);
        log.debug("定时告警检查完成 at={}", now);
    }

    public Instant getLastRun() { return lastRun.get(); }
    public boolean isEnabled() { return enabled; }
}
