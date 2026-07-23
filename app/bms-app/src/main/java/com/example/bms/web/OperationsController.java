package com.example.bms.web;

import com.example.bms.event.EventQueryService;
import com.example.bms.event.EventSource;
import com.example.bms.reporting.ProtocolStatisticsJdbcRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** 协议履历、通知、报表、状态、审计和用户管理页面。 */
@Controller
public class OperationsController {
    private final EventQueryService events;
    private final OperationsViewService operations;
    private final DashboardService dashboard;
    private final ProtocolStatisticsJdbcRepository statistics;

    public OperationsController(EventQueryService events, OperationsViewService operations,
                                DashboardService dashboard, ProtocolStatisticsJdbcRepository statistics) {
        this.events = events;
        this.operations = operations;
        this.dashboard = dashboard;
        this.statistics = statistics;
    }

    @GetMapping("/history/syslog")
    public String syslog(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("events", events.bySource(EventSource.SYSLOG, page));
        model.addAttribute("historyTitle", "Syslog受信履歴");
        model.addAttribute("historyType", "syslog");
        return "history/protocol";
    }

    @GetMapping("/history/snmp-trap")
    public String snmpTrap(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("events", events.bySource(EventSource.SNMP_TRAP, page));
        model.addAttribute("historyTitle", "SNMP Trap受信履歴");
        model.addAttribute("historyType", "snmp-trap");
        return "history/protocol";
    }

    @GetMapping("/history/snmp-get")
    public String snmpGet(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("events", events.bySource(EventSource.SNMP_GET, page));
        model.addAttribute("historyTitle", "SNMP GET結果一覧");
        model.addAttribute("historyType", "snmp-get");
        return "history/protocol";
    }

    @GetMapping("/history/tcp-ping")
    public String tcpPing(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("results", operations.tcpPingResults(page));
        return "history/tcp-ping";
    }

    @GetMapping("/notifications")
    public String notifications(Model model) {
        model.addAttribute("targets", operations.notificationTargets());
        return "operations/notifications";
    }

    @GetMapping({"/reports", "/reports/trends"})
    public String reports(Model model) {
        model.addAttribute("dashboard", dashboard.getDashboard());
        model.addAttribute("sourceCounts", statistics.countEventsBySource());
        return "operations/report";
    }

    @GetMapping("/system/status")
    public String status(Model model) {
        model.addAttribute("status", operations.systemStatus());
        return "operations/status";
    }

    @GetMapping("/audit")
    public String audit(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("logs", operations.auditLogs(page));
        return "operations/audit";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", operations.users());
        return "operations/users";
    }

    @GetMapping("/error/403")
    public String forbidden(Model model) {
        model.addAttribute("status", 403);
        model.addAttribute("title", "この操作を実行する権限がありません");
        model.addAttribute("message", "必要なロールを持つアカウントでログインしてください。");
        return "error/error";
    }
}
