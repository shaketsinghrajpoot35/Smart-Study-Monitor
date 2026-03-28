package com.studyfocus.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class FocusRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private int focusScore;
    private boolean faceDetected;
    private boolean drowsy;
    private int noiseLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public FocusRecord() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getFocusScore() { return focusScore; }
    public void setFocusScore(int focusScore) { this.focusScore = focusScore; }

    public boolean isFaceDetected() { return faceDetected; }
    public void setFaceDetected(boolean faceDetected) { this.faceDetected = faceDetected; }

    public boolean isDrowsy() { return drowsy; }
    public void setDrowsy(boolean drowsy) { this.drowsy = drowsy; }

    public int getNoiseLevel() { return noiseLevel; }
    public void setNoiseLevel(int noiseLevel) { this.noiseLevel = noiseLevel; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
