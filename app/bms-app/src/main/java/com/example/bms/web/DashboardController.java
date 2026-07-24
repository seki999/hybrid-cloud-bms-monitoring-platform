package com.example.bms.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Dashboard HTTP Adapter。 */
@Controller
/**
 * 提供仪表盘首页的 MVC 入口，把统计查询结果放入视图模型并选择 Thymeleaf 模板。
 * 控制器保持轻量，聚合与计算由 DashboardService 完成，便于独立测试和复用。
 */
public class DashboardController {
    private final DashboardService service;
    public DashboardController(DashboardService service) { this.service = service; }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("dashboard", service.getDashboard());
        return "dashboard";
    }
}
