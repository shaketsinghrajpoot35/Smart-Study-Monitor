package com.studyfocus.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TimerService {

    public enum Mode {
        STUDY, BREAK, IDLE
    }

    private Mode mode = Mode.IDLE;
    private int remainingSeconds;

    private int studySeconds;
    private int breakSeconds;

    private final TodoService todoService;

    public TimerService(TodoService todoService) {
        this.todoService = todoService;
    }

    /* ================= START FROM TODO ================= */
    // 🔥 called when user clicks "Start Todo"
    public synchronized void startForTodo(int studyMin, int breakMin) {
        this.studySeconds = studyMin * 60;
        this.breakSeconds = breakMin * 60;

        this.mode = Mode.STUDY;
        this.remainingSeconds = studySeconds;

        System.out.println("▶ STUDY started from TODO");
    }

    /* ================= TIMER TICK ================= */
    @Scheduled(fixedRate = 1000)
    public synchronized void tick() {

        if (mode == Mode.IDLE) return;

        // ⏱ TRACK ACTIVE TODO TIME
        if (mode == Mode.STUDY) {
            todoService.addStudySecond();
        }
        else if (mode == Mode.BREAK) {
            todoService.addBreakSecond();
        }

        // ⏱ COUNTDOWN
        if (remainingSeconds > 0) {
            remainingSeconds--;
            return;
        }

        /* ================= MODE SWITCH ================= */

        // STUDY → BREAK
        if (mode == Mode.STUDY) {
            mode = Mode.BREAK;
            remainingSeconds = breakSeconds;
            System.out.println("⏸ BREAK started");
            return;
        }

        // BREAK → DONE
        if (mode == Mode.BREAK) {
            System.out.println("✅ TODO COMPLETED");

            todoService.getActiveTodo();  // ✅ AUTO COMPLETE
            mode = Mode.IDLE;
            remainingSeconds = 0;
        }
    }

    /* ================= GETTERS ================= */
    public Mode getMode() {
        return mode;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public boolean isBreakTime() {
        return mode == Mode.BREAK;
    }

    public boolean isRunning() {
        return mode != Mode.IDLE;
    }
}
