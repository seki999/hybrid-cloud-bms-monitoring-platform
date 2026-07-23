package com.example.bms.web;

import com.example.bms.event.EventQueryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/** Event 一览和详情页面。 */
@Controller
public class EventController {
    private final EventQueryService service;
    public EventController(EventQueryService service) { this.service = service; }

    @GetMapping("/events")
    public String list(@RequestParam(defaultValue = "") String q,
                       @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("events", service.search(q, page));
        model.addAttribute("q", q);
        return "events/list";
    }

    @GetMapping("/events/{id}")
    public String detail(@PathVariable long id, Model model) {
        model.addAttribute("event", service.get(id));
        return "events/detail";
    }
}

