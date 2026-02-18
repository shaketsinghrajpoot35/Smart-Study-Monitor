//package com.studyfocus.model;
//
//public class Progress {
//
//    private long studyTime;
//    private long breakTime;
//    private long idleTime;
//    private int alarmsTriggered;
//    private int sessionsCompleted;
//
//    public long getStudyTime() { return studyTime; }
//    public void addStudyTime(long sec) { this.studyTime += sec; }
//
//    public long getBreakTime() { return breakTime; }
//    public void addBreakTime(long sec) { this.breakTime += sec; }
//
//    public long getIdleTime() { return idleTime; }
//    public void addIdleTime(long sec) { this.idleTime += sec; }
//
//    public int getAlarmsTriggered() { return alarmsTriggered; }
//    public void incAlarms() { this.alarmsTriggered++; }
//
//    public int getSessionsCompleted() { return sessionsCompleted; }
//    public void incSessions() { this.sessionsCompleted++; }
//}


package com.studyfocus.model;

import java.time.LocalDateTime;

public class Progress {
    public String state;
    public LocalDateTime time;

    public Progress(String state, LocalDateTime time) {
        this.state = state;
        this.time = time;
    }
}
