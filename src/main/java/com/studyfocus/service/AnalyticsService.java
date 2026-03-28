package com.studyfocus.service;

import com.studyfocus.entity.DailyReport;
import com.studyfocus.entity.User;
import com.studyfocus.repository.DailyReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AnalyticsService {

    private final DailyReportRepository dailyReportRepository;

    public AnalyticsService(DailyReportRepository dailyReportRepository) {
        this.dailyReportRepository = dailyReportRepository;
    }

    public List<DailyReport> getWeeklyAnalytics(User user) {
        LocalDate today = LocalDate.now();
        LocalDate lastWeek = today.minusDays(7);
        return dailyReportRepository.findByUserAndDateBetweenOrderByDateAsc(user, lastWeek, today);
    }

    public List<DailyReport> getMonthlyAnalytics(User user) {
        LocalDate today = LocalDate.now();
        LocalDate lastMonth = today.minusDays(30);
        return dailyReportRepository.findByUserAndDateBetweenOrderByDateAsc(user, lastMonth, today);
    }
}
