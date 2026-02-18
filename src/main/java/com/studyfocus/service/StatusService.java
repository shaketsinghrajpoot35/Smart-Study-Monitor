package com.studyfocus.service;

import com.studyfocus.model.StatusInfo;
import org.springframework.stereotype.Service;

@Service
public class StatusService {

    private final DrowsinessMonitorService monitor;
    private final AlarmService alarm;
    private final TimerService timer;
    private final NoiseMonitorService noiseMonitor;

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

    public StatusInfo getCurrentStatus() {

        StatusInfo status = new StatusInfo();

        // ===== TIMER STATUS =====
        String mode = timer.getMode().name();
        status.setCurrentStatus(mode);
        status.setRemainingSeconds(timer.getRemainingSeconds());

        // ===== FACE / DROWSY =====
        boolean faceDetected = monitor.isFaceDetected();
        boolean drowsy = monitor.isDrowsy();

        status.setFaceDetected(faceDetected);
        status.setDrowsy(drowsy);

        // ===== NOISE =====
        boolean noisy = noiseMonitor.isNoisy();
        status.setNoisy(noisy);

        if (noisy) {
            status.setNoiseMessage(
                "⚠ Environment noise is high. Please move to a quiet area."
            );
        }

        // ==================================================
        // 🔥 NEW FEATURE: FOCUS SCORE (ADD ONLY)
        // ==================================================
        int focusScore = 0;

        if (faceDetected) {
            focusScore += 40;
        }

        if (!drowsy) {
            focusScore += 30;
        }

        if (!noisy) {
            focusScore += 20;
        }

        if ("STUDY".equals(mode)) {
            focusScore += 10;
        }

        status.setFocusScore(focusScore);

        if (focusScore >= 80) {
            status.setFocusLabel("🔥 Excellent Focus");
        } else if (focusScore >= 60) {
            status.setFocusLabel("🙂 Good Focus");
        } else if (focusScore >= 40) {
            status.setFocusLabel("⚠ Low Focus");
        } else {
            status.setFocusLabel("❌ Very Low Focus");
        }

        // ==================================================
        // ===== FINAL ALARM LOGIC (UNCHANGED)
        // ==================================================
        if (!timer.isBreakTime()) {

            if (!faceDetected) {
                alarm.play("noface");
            }
            else if (drowsy) {
                alarm.play("drowsy");
            }
            else {
                alarm.stop();
            }

        } else {
            alarm.stop(); // break time always silent
        }

        return status;
    }
}
