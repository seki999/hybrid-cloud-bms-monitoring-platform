package com.example.bms.audit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** 将业务操作写入不可变审计表；调用方只传脱敏摘要。 */
@Service
public class AuditService {
    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) { this.repository = repository; }

    /**
     * 保存审计记录。
     *
     * @param actor 操作者或 system
     * @param action 稳定动作代码
     * @param resourceType 资源类型
     * @param resourceId 资源主键
     * @param detail 不含 Secret 的说明
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void record(String actor, String action, String resourceType, String resourceId, String detail) {
        repository.save(new AuditLog(actor, action, resourceType, resourceId, detail));
    }
}

