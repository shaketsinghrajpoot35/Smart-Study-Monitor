package com.studyfocus.service;

import com.studyfocus.entity.DailyReport;
import com.studyfocus.entity.FocusRecord;
import com.studyfocus.entity.User;
import com.studyfocus.repository.DailyReportRepository;
import com.studyfocus.repository.FocusRecordRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DataSaverService {

    private final FocusRecordRepository focusRecordRepository;
    private final DailyReportRepository dailyReportRepository;

    public DataSaverService(FocusRecordRepository focusRecordRepository, DailyReportRepository dailyReportRepository) {
        this.focusRecordRepository = focusRecordRepository;
        this.dailyReportRepository = dailyReportRepository;
    }

    @Async
    @Transactional
    public void saveFocusSnapshot(User user, int focusScore, boolean faceDetected, boolean drowsy, int noiseLevel) {
        if (user == null) return;

        FocusRecord record = new FocusRecord();
        record.setUser(user);
        record.setTimestamp(LocalDateTime.now());
        record.setFocusScore(focusScore);
        record.setFaceDetected(faceDetected);
        record.setDrowsy(drowsy);
        record.setNoiseLevel(noiseLevel);
        
        focusRecordRepository.save(record);

        // Update Daily Report moving average
        updateDailyReportFocus(user, focusScore);
    }

    @Async
    @Transactional
    public void addStudyOrBreakTime(User user, boolean isStudy, int seconds) {
        if (user == null) return;
        
        DailyReport report = getTodayReport(user);
        if (isStudy) {
            report.setTotalStudySeconds(report.getTotalStudySeconds() + seconds);
        } else {
            report.setTotalBreakSeconds(report.getTotalBreakSeconds() + seconds);
        }
        
        dailyReportRepository.save(report);
    }

    private void updateDailyReportFocus(User user, int newScore) {
        DailyReport report = getTodayReport(user);
        
        // Simple moving average logic (assuming 1 update per 60 secs)
        int currentAvg = report.getAverageFocusScore();
        int activeSeconds = report.getTotalStudySeconds();
        
        if (activeSeconds == 0) {
            report.setAverageFocusScore(newScore);
        } else {
            // Rough approximation
            int updatedAvg = (currentAvg * 9 + newScore) / 10;
            report.setAverageFocusScore(updatedAvg);
        }
        
        dailyReportRepository.save(report);
    }

    private DailyReport getTodayReport(User user) {
        LocalDate today = LocalDate.now();
        return dailyReportRepository.findByUserAndDate(user, today)
                .orElseGet(() -> {
                    DailyReport newReport = new DailyReport();
                    newReport.setUser(user);
                    newReport.setDate(today);
                    newReport.setAverageFocusScore(0);
                    newReport.setTotalStudySeconds(0);
                    newReport.setTotalBreakSeconds(0);
                    return dailyReportRepository.save(newReport);
                });
    }
}
