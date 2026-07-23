package com.example.bms.monitoring;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** 阈值规则访问层。 */
public interface MonitoringRuleRepository extends JpaRepository<MonitoringRule, Long> {
    List<MonitoringRule> findAllByOrderByNameAsc();
    Optional<MonitoringRule> findFirstByTargetAndMetricNameAndEnabledTrue(
            MonitoringTarget target, String metricName);
}

