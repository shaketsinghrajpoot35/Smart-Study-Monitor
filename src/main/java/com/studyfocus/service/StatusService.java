package com.studyfocus.service;

import com.studyfocus.entity.User;
import com.studyfocus.model.StatusInfo;
import org.springframework.stereotype.Service;

@Service
public class StatusService {

    private final DrowsinessMonitorService monitor;
    private final AlarmService alarm;
    private final TimerService timer;
    private final NoiseMonitorService noiseMonitor;

    // AI Adaptive Break Tracking
    private final java.util.Map<Long, Integer> userDrowsySeconds = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.Map<Long, java.util.List<Integer>> userFocusHistory = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.Map<Long, Long> userLastSuggestionTime = new java.util.concurrent.ConcurrentHashMap<>();

    public StatusService(
            DrowsinessMonitorService monitor,
            AlarmService alarm,
            TimerService timer,
            NoiseMonitorService noiseMonitor
    ) {
        this.monitor = monitor;
        this.alarm = alarm;
        this.timer = timer;
        this.noiseMonitor = noiseMonitor;
    }

    public StatusInfo getCurrentStatus(User user) {
        StatusInfo status = new StatusInfo();

        // ===== TIMER STATUS =====
        TimerService.TimerState tState = timer.getTimerForUser(user);
        String mode = tState.mode.name();
        status.setCurrentStatus(mode);
        status.setRemainingSeconds(tState.remainingSeconds);
        status.setTimerMode(mode);

        // ===== FACE / DROWSY =====
        boolean faceDetected = monitor.isFaceDetected();
        boolean drowsy = monitor.isDrowsy();

        status.setFaceDetected(faceDetected);
        status.setDrowsy(drowsy);

        // ===== NOISE =====
        boolean noisy = noiseMonitor.isNoisy();
        status.setNoisy(noisy);

        if (noisy) {
            status.setNoiseMessage("⚠ Environment noise is high. Please move to a quiet area.");
        }

        // ===== FOCUS SCORE CALCULATION =====
        int focusScore = 0;
        if (faceDetected) focusScore += 40;
        if (!drowsy) focusScore += 30;
        if (!noisy) focusScore += 20;
        if ("STUDY".equals(mode)) focusScore += 10;

        status.setFocusScore(focusScore);

        if (focusScore >= 80) status.setFocusLabel("🔥 Excellent Focus");
        else if (focusScore >= 60) status.setFocusLabel("🙂 Good Focus");
        else if (focusScore >= 40) status.setFocusLabel("⚠ Low Focus");
        else status.setFocusLabel("❌ Very Low Focus");

        // ==================================================
        // 🧠 AI ADAPTIVE BREAK LOGIC
        // ==================================================
        if (user != null && "STUDY".equals(mode)) {
            long userId = user.getId();
            long now = System.currentTimeMillis();
            long lastSuggestion = userLastSuggestionTime.getOrDefault(userId, 0L);

            // Cooldown of 10 minutes between suggestions
            if (now - lastSuggestion > 600000) { 
                
                // Track Drowsiness (Fatigue)
                int drowsySec = userDrowsySeconds.getOrDefault(userId, 0);
                if (drowsy) {
                    drowsySec++;
                    userDrowsySeconds.put(userId, drowsySec);
                } else {
                    if (drowsySec > 0) userDrowsySeconds.put(userId, drowsySec - 1);
                }

                // Threshold Check: 15+ seconds of drowsiness
                if (drowsySec >= 15) {
                    status.setSuggestBreak(true);
                    status.setBreakRecommendation("AI detected sign of fatigue. High drowsiness levels detected. We recommend a short break.");
                    userLastSuggestionTime.put(userId, now);
                    userDrowsySeconds.put(userId, 0); // reset after suggestion
                }

                // Track Focus History (Trend)
                java.util.List<Integer> history = userFocusHistory.computeIfAbsent(userId, k -> new java.util.ArrayList<>());
                history.add(focusScore);
                if (history.size() > 60) history.remove(0); // keep last 60 refreshes (~1-2 mins)

                if (history.size() >= 30) {
                    double avgFocus = history.stream().mapToInt(Integer::intValue).average().orElse(100.0);
                    if (avgFocus < 45) {
                        status.setSuggestBreak(true);
                        status.setBreakRecommendation("Your focus has been consistently low for the past few minutes. A refreshment break might help!");
                        userLastSuggestionTime.put(userId, now);
                        history.clear(); // reset after suggestion
                    }
                }
            }
        } else if (user != null) {
            // Reset counters when not in study mode
            userDrowsySeconds.put(user.getId(), 0);
            if (userFocusHistory.containsKey(user.getId())) userFocusHistory.get(user.getId()).clear();
        }

        // ===== FINAL ALARM LOGIC (TRIGGERED ON FRONTEND) =====
        boolean isBreakTime = tState.mode == TimerService.Mode.BREAK;
        if (!isBreakTime) {
            if (!faceDetected) {
                status.setAlarmType("noface");
            }
            else if (drowsy) {
                status.setAlarmType("drowsy");
            }
            else {
                status.setAlarmType(null);
            }
        } else {
            status.setAlarmType(null); // silent during break
        }

        return status;
    }
}
