package com.studyfocus.controller;

import com.studyfocus.service.TimerService;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class TimerController {

    private final TimerService timerService;

    public TimerController(TimerService timerService) {
        this.timerService = timerService;
    }

    // ===== START TIMER =====
    @PostMapping("/timer/start")
    public void start(
            @RequestParam int study,
            @RequestParam int brk
    ) {
    	timerService.startForTodo(study, brk);
    }

    // ===== GET TIMER STATUS =====
    @GetMapping("/timer")
    public Map<String, Object> getTimer() {
        return Map.of(
                "mode", timerService.getMode().name(),
                "remaining", timerService.getRemainingSeconds()
        );
    }
}
