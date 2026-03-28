package com.studyfocus.controller;

import com.studyfocus.entity.User;
import com.studyfocus.model.StatusInfo;
import com.studyfocus.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    private final StatusService statusService;
    private final com.studyfocus.service.DataSaverService dataSaverService;
    private final com.studyfocus.repository.UserRepository userRepository;
    private long lastSavedTime = 0;

    @Autowired
    public StatusController(StatusService statusService, 
                            com.studyfocus.service.DataSaverService dataSaverService, 
                            com.studyfocus.repository.UserRepository userRepository) {
        this.statusService = statusService;
        this.dataSaverService = dataSaverService;
        this.userRepository = userRepository;
    }

    @GetMapping("/status")
    public StatusInfo getStatus(org.springframework.security.core.Authentication auth) {
        User user = null;
        if (auth != null && auth.isAuthenticated()) {
            user = userRepository.findByUsername(auth.getName()).orElse(null);
        }

        StatusInfo info = statusService.getCurrentStatus(user);
        long now = System.currentTimeMillis();
        
        // Save focus data historically every 60 seconds
        if (now - lastSavedTime > 60000 && user != null) {
            int score = (info.isFaceDetected() && !info.isDrowsy()) ? 90 : 50;
            info.setFocusScore(score);
            dataSaverService.saveFocusSnapshot(user, score, info.isFaceDetected(), info.isDrowsy(), info.isNoisy() ? 80 : 30);
            lastSavedTime = now;
        }
        return info;
    }
}
