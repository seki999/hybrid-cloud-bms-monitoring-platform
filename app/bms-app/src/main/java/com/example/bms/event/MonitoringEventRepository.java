package com.example.bms.event;

import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/** Event は追記主体であり、検索は発生時刻降順の索引を利用する。 */
public interface MonitoringEventRepository extends JpaRepository<MonitoringEvent, Long> {
    @EntityGraph(attributePaths = {"device", "target"})
    Page<MonitoringEvent> findByMessageContainingIgnoreCase(String query, Pageable pageable);
    @EntityGraph(attributePaths = {"device", "target"})
    Page<MonitoringEvent> findBySource(EventSource source, Pageable pageable);
    @EntityGraph(attributePaths = {"device", "target"})
    List<MonitoringEvent> findTop10ByOrderByOccurredAtDesc();
    boolean existsByFingerprintAndOccurredAtAfter(String fingerprint, Instant after);
    long countBySource(EventSource source);
    long countByOccurredAtAfter(Instant after);
}
