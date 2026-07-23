package com.example.bms.monitoring;

import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** TCP Ping 结果访问层。 */
public interface TcpPingResultRepository extends JpaRepository<TcpPingResult, Long> {
    Page<TcpPingResult> findAllByOrderByCheckedAtDesc(Pageable pageable);
    long countByCheckedAtAfter(Instant after);
    long countBySuccessTrueAndCheckedAtAfter(Instant after);
}

