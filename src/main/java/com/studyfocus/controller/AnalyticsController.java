package com.studyfocus.controller;

import com.studyfocus.entity.DailyReport;
import com.studyfocus.entity.User;
import com.studyfocus.repository.UserRepository;
import com.studyfocus.service.AnalyticsService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final UserRepository userRepository;

    public AnalyticsController(AnalyticsService analyticsService, UserRepository userRepository) {
        this.analyticsService = analyticsService;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        return userRepository.findByUsername(auth.getName()).orElse(null);
    }

    @GetMapping("/weekly")
    public List<DailyReport> getWeekly(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        return analyticsService.getWeeklyAnalytics(user);
    }

    @GetMapping("/monthly")
    public List<DailyReport> getMonthly(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        return analyticsService.getMonthlyAnalytics(user);
    }

    @GetMapping("/today")
    public DailyReport getToday(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        return analyticsService.getTodayAnalytics(user);
    }
}
