package com.studyfocus.repository;

import com.studyfocus.entity.DailyReport;
import com.studyfocus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {
    Optional<DailyReport> findByUserAndDate(User user, LocalDate date);
    List<DailyReport> findByUserAndDateBetweenOrderByDateAsc(User user, LocalDate startDate, LocalDate endDate);
}
