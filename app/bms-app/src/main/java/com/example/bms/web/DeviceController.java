package com.example.bms.web;

import com.example.bms.device.Device;
import com.example.bms.device.DeviceForm;
import com.example.bms.device.DeviceService;
import com.example.bms.device.DeviceType;
import com.example.bms.device.OperationalStatus;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** 监视对象设备的 SSR Controller；所有业务和事务交给 DeviceService。 */
@Controller
@RequestMapping("/devices")
public class DeviceController {
    private final DeviceService service;
    public DeviceController(DeviceService service) { this.service = service; }

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String q,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "name") String sort,
                       @RequestParam(defaultValue = "asc") String direction, Model model) {
        model.addAttribute("devices", service.search(q, page, 20, sort, direction));
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        return "devices/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable long id, Model model) {
        Device device = service.get(id);
        model.addAttribute("device", device);
        model.addAttribute("targets", service.targets(device));
        return "devices/detail";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String createForm(Model model) {
        model.addAttribute("deviceForm", new DeviceForm());
        addOptions(model);
        return "devices/form";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@Valid @ModelAttribute DeviceForm deviceForm, BindingResult result,
                         Principal principal, Model model) {
        if (result.hasErrors()) { addOptions(model); return "devices/form"; }
        Device saved = service.create(deviceForm, principal.getName());
        return "redirect:/devices/" + saved.getId() + "?saved";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable long id, Model model) {
        model.addAttribute("deviceForm", DeviceForm.from(service.get(id)));
        model.addAttribute("deviceId", id);
        addOptions(model);
        return "devices/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable long id, @Valid @ModelAttribute DeviceForm deviceForm,
                         BindingResult result, Principal principal, Model model) {
        if (result.hasErrors()) { model.addAttribute("deviceId", id); addOptions(model); return "devices/form"; }
        service.update(id, deviceForm, principal.getName());
        return "redirect:/devices/" + id + "?saved";
    }

    private void addOptions(Model model) {
        model.addAttribute("deviceTypes", DeviceType.values());
        model.addAttribute("statuses", OperationalStatus.values());
    }
}

