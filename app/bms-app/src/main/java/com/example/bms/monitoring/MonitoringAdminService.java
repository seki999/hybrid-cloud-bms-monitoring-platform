package com.example.bms.monitoring;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 监视规则设置页面的查询服务。 */
@Service
public class MonitoringAdminService {
    private final MonitoringRuleRepository ruleRepository;

    public MonitoringAdminService(MonitoringRuleRepository ruleRepository) { this.ruleRepository = ruleRepository; }

    @Transactional(readOnly = true)
    public List<MonitoringRule> rules() {
        List<MonitoringRule> rules = ruleRepository.findAllByOrderByNameAsc();
        rules.forEach(rule -> rule.getTarget().getDevice().getName());
        return rules;
    }
}

