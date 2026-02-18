package com.studyfocus.controller;

import com.studyfocus.model.StatusInfo;
import com.studyfocus.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    private final StatusService statusService;

    @Autowired
    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping("/status")
    public StatusInfo getStatus() {
        return statusService.getCurrentStatus();
    }
}
