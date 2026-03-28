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
    private final java.util.Map<Long, Long> userLastSavedTime = new java.util.concurrent.ConcurrentHashMap<>();

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

        if (user != null) {
            long lastSave = userLastSavedTime.getOrDefault(user.getId(), 0L);
            if (now - lastSave > 60000) {
                // Use actual computed focus score — NOT hardcoded
                int score = info.getFocusScore();
                int noiseLevel = info.isNoisy() ? 80 : 20;
                dataSaverService.saveFocusSnapshot(user, score, info.isFaceDetected(), info.isDrowsy(), noiseLevel);
                userLastSavedTime.put(user.getId(), now);
            }
        }
        return info;
    }
}
