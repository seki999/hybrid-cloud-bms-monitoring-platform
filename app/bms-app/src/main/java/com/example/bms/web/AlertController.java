package com.example.bms.web;

import com.example.bms.alert.Alert;
import com.example.bms.alert.AlertService;
import java.security.Principal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Alert 查询、确认画面、确认/关闭操作。 */
@Controller
public class AlertController {
    private final AlertService service;
    public AlertController(AlertService service) { this.service = service; }

    @GetMapping("/alerts")
    public String list(@RequestParam(defaultValue = "") String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("alerts", service.search(q, page));
        model.addAttribute("q", q);
        return "alerts/list";
    }

    @GetMapping("/alerts/{id}")
    public String detail(@PathVariable long id, Model model) {
        Alert alert = service.get(id);
        model.addAttribute("alert", alert);
        model.addAttribute("history", service.history(alert));
        return "alerts/detail";
    }

    @GetMapping("/alerts/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public String confirm(@PathVariable long id, Model model) {
        model.addAttribute("alert", service.get(id));
        return "alerts/confirm";
    }

    @PostMapping("/alerts/{id}/acknowledge")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public String acknowledge(@PathVariable long id, Principal principal) {
        service.acknowledge(id, principal.getName());
        return "redirect:/alerts/" + id + "?acknowledged";
    }

    @PostMapping("/alerts/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public String close(@PathVariable long id, Principal principal) {
        service.close(id, principal.getName());
        return "redirect:/alerts/" + id + "?closed";
    }
}

