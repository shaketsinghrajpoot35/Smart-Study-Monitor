package com.studyfocus.controller;

import com.studyfocus.entity.User;
import com.studyfocus.repository.UserRepository;
import com.studyfocus.service.TimerService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class TimerController {

    private final TimerService timerService;
    private final UserRepository userRepository;

    public TimerController(TimerService timerService, UserRepository userRepository) {
        this.timerService = timerService;
        this.userRepository = userRepository;
    }

    private User getUser(Authentication auth) {
        if (auth == null) return null;
        return userRepository.findByUsername(auth.getName()).orElse(null);
    }

    @PostMapping("/timer/start")
    public void start(
            Authentication auth,
            @RequestParam int study,
            @RequestParam int brk
    ) {
        User user = getUser(auth);
        if (user != null) {
            timerService.startForTodo(user, null, study, brk);
        }
    }

    @PostMapping("/timer/stop")
    public void stop(Authentication auth) {
        User user = getUser(auth);
        if (user != null) {
            timerService.stopTimer(user);
        }
    }

    @PostMapping("/timer/switch-to-break")
    public void switchToBreak(Authentication auth) {
        User user = getUser(auth);
        if (user != null) {
            timerService.switchToBreak(user);
        }
    }

    @GetMapping("/timer")
    public Map<String, Object> getTimer(Authentication auth) {
        User user = getUser(auth);
        TimerService.TimerState state = timerService.getTimerForUser(user);

        return Map.of(
                "mode", state.mode.name(),
                "remaining", state.remainingSeconds
        );
    }
}
