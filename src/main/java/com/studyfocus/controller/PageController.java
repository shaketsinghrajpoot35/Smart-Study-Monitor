package com.studyfocus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "index";   // templates/index.html
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // templates/dashboard.html
    }
    
    @GetMapping("/history")
    public String history() {
        return "history"; // templates/history.html
    }
}
