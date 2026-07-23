package com.example.bms.alert;

import com.example.bms.audit.AuditService;
import com.example.bms.common.domain.AlertStatus;
import com.example.bms.common.exception.BusinessException;
import com.example.bms.common.exception.ResourceNotFoundException;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 告警查询与操作员状态迁移的事务边界。 */
@Service
public class AlertService {
    private final AlertRepository repository;
    private final AlertHistoryRepository historyRepository;
    private final AuditService auditService;

    public AlertService(AlertRepository repository, AlertHistoryRepository historyRepository,
                        AuditService auditService) {
        this.repository = repository;
        this.historyRepository = historyRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Page<Alert> search(String query, int page) {
        return repository.findByTitleContainingIgnoreCase(query == null ? "" : query.trim(),
                PageRequest.of(Math.max(page, 0), 20, Sort.by(Sort.Direction.DESC, "lastOccurredAt")));
    }

    @Transactional(readOnly = true)
    public Alert get(long id) {
        Alert alert = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("アラートが見つかりません"));
        // 详情页在事务结束后渲染，因此在这里初始化必要的关联，避免模板访问懒加载代理。
        alert.getDevice().getName();
        // 协议直接上报的告警不一定来自阈值规则，因此 rule 是可空关联。
        if (alert.getRule() != null) {
            alert.getRule().getName();
        }
        return alert;
    }

    @Transactional(readOnly = true)
    public List<AlertHistory> history(Alert alert) { return historyRepository.findByAlertOrderByCreatedAtDesc(alert); }

    /** 活跃告警确认；已经关闭的记录不能倒退回确认状态。 */
    @Transactional
    public void acknowledge(long id, String actor) {
        Alert alert = get(id);
        if (alert.getStatus() == AlertStatus.CLOSED) throw new BusinessException("終了済みアラートは確認できません");
        AlertStatus before = alert.getStatus();
        alert.acknowledge(actor, Instant.now());
        historyRepository.save(new AlertHistory(alert, before, AlertStatus.ACKNOWLEDGED, actor, "画面から確認"));
        auditService.record(actor, "ALERT_ACKNOWLEDGED", "ALERT", String.valueOf(id), alert.getTitle());
    }

    /** 手工关闭并保留生命周期履历。 */
    @Transactional
    public void close(long id, String actor) {
        Alert alert = get(id);
        if (alert.getStatus() == AlertStatus.CLOSED) throw new BusinessException("アラートは既に終了しています");
        AlertStatus before = alert.getStatus();
        alert.close(Instant.now());
        historyRepository.save(new AlertHistory(alert, before, AlertStatus.CLOSED, actor, "画面から終了"));
        auditService.record(actor, "ALERT_CLOSED", "ALERT", String.valueOf(id), alert.getTitle());
    }
}
