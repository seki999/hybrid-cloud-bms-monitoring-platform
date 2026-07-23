package com.example.bms.event;

import com.example.bms.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Event 一览、协议履历与详情的只读查询服务。 */
@Service
public class EventQueryService {
    private final MonitoringEventRepository repository;

    public EventQueryService(MonitoringEventRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public Page<MonitoringEvent> search(String query, int page) {
        return repository.findByMessageContainingIgnoreCase(query == null ? "" : query.trim(),
                PageRequest.of(Math.max(page, 0), 20, Sort.by(Sort.Direction.DESC, "occurredAt")));
    }

    @Transactional(readOnly = true)
    public Page<MonitoringEvent> bySource(EventSource source, int page) {
        return repository.findBySource(source,
                PageRequest.of(Math.max(page, 0), 20, Sort.by(Sort.Direction.DESC, "occurredAt")));
    }

    @Transactional(readOnly = true)
    public MonitoringEvent get(long id) {
        MonitoringEvent event = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("イベントが見つかりません"));
        if (event.getDevice() != null) event.getDevice().getName();
        if (event.getTarget() != null) event.getTarget().getName();
        return event;
    }
}

