package com.example.bms.web;

import com.example.bms.monitoring.MonitoringAdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** 监视规则设置页面。 */
@Controller
public class MonitoringRuleController {
    private final MonitoringAdminService service;
    public MonitoringRuleController(MonitoringAdminService service) { this.service = service; }

    @GetMapping("/monitoring-rules")
    public String rules(Model model) {
        model.addAttribute("rules", service.rules());
        return "monitoring/rules";
    }
}

