package com.example.bms.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** 登录与根路径导航。 */
@Controller
public class LoginController {
    @GetMapping("/")
    public String root() { return "redirect:/dashboard"; }

    @GetMapping("/login")
    public String login() { return "login"; }
}

