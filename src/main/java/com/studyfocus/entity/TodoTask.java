package com.studyfocus.entity;

import jakarta.persistence.*;
import com.studyfocus.model.TodoStatus;
import com.studyfocus.model.TodoType;

@Entity
public class TodoTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;
    private String topic;

    private int plannedStudyMinutes;
    private int plannedBreakMinutes;

    private int actualStudySeconds = 0;
    private int actualBreakSeconds = 0;

    @Enumerated(EnumType.STRING)
    private TodoStatus status = TodoStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private TodoType type;
    
    private boolean active;

    // ===== GETTERS / SETTERS =====

    public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() { return id; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public int getPlannedStudyMinutes() { return plannedStudyMinutes; }
    public void setPlannedStudyMinutes(int plannedStudyMinutes) {
        this.plannedStudyMinutes = plannedStudyMinutes;
    }

    public int getPlannedBreakMinutes() { return plannedBreakMinutes; }
    public void setPlannedBreakMinutes(int plannedBreakMinutes) {
        this.plannedBreakMinutes = plannedBreakMinutes;
    }

    public int getActualStudySeconds() { return actualStudySeconds; }
    public void setActualStudySeconds(int actualStudySeconds) {
        this.actualStudySeconds = actualStudySeconds;
    }

    public int getActualBreakSeconds() { return actualBreakSeconds; }
    public void setActualBreakSeconds(int actualBreakSeconds) {
        this.actualBreakSeconds = actualBreakSeconds;
    }

    public TodoStatus getStatus() { return status; }
    public void setStatus(TodoStatus status) { this.status = status; }

    public TodoType getType() { return type; }
    public void setType(TodoType type) { this.type = type; }
}
