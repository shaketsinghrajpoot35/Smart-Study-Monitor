package com.studyfocus.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class DailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    
    private int totalStudySeconds = 0;
    private int totalBreakSeconds = 0;
    private int averageFocusScore = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public DailyReport() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getTotalStudySeconds() { return totalStudySeconds; }
    public void setTotalStudySeconds(int totalStudySeconds) { this.totalStudySeconds = totalStudySeconds; }

    public int getTotalBreakSeconds() { return totalBreakSeconds; }
    public void setTotalBreakSeconds(int totalBreakSeconds) { this.totalBreakSeconds = totalBreakSeconds; }

    public int getAverageFocusScore() { return averageFocusScore; }
    public void setAverageFocusScore(int averageFocusScore) { this.averageFocusScore = averageFocusScore; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
