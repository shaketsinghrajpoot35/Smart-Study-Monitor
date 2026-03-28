package com.studyfocus.service;

import com.studyfocus.entity.User;
import com.studyfocus.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TimerService {

    public enum Mode {
        STUDY, BREAK, IDLE
    }

    public static class TimerState {
        public Mode mode = Mode.IDLE;
        public int remainingSeconds;
        public int studySeconds;
        public int breakSeconds;
        public Long activeTodoId;
        public User user;
    }

    private final Map<Long, TimerState> userTimers = new ConcurrentHashMap<>();
    
    private final TodoService todoService;
    private final DataSaverService dataSaverService;
    private final UserRepository userRepository;

    public TimerService(TodoService todoService, DataSaverService dataSaverService, UserRepository userRepository) {
        this.todoService = todoService;
        this.dataSaverService = dataSaverService;
        this.userRepository = userRepository;
    }

    public TimerState getTimerForUser(User user) {
        if (user == null) return new TimerState();
        return userTimers.computeIfAbsent(user.getId(), k -> {
            TimerState state = new TimerState();
            state.user = user;
            return state;
        });
    }

    public synchronized void startForTodo(User user, Long todoId, int studyMin, int breakMin) {
        if (user == null) return;
        TimerState state = getTimerForUser(user);
        state.studySeconds = studyMin * 60;
        state.breakSeconds = breakMin * 60;
        state.mode = Mode.STUDY;
        state.remainingSeconds = state.studySeconds;
        state.activeTodoId = todoId;
    }

    @Scheduled(fixedRate = 1000)
    public synchronized void tick() {
        for (TimerState state : userTimers.values()) {
            if (state.mode == Mode.IDLE) continue;

            if (state.mode == Mode.STUDY) {
                todoService.addStudySecond(state.user);
                dataSaverService.addStudyOrBreakTime(state.user, true, 1);
            } else if (state.mode == Mode.BREAK) {
                todoService.addBreakSecond(state.user);
                dataSaverService.addStudyOrBreakTime(state.user, false, 1);
            }

            if (state.remainingSeconds > 0) {
                state.remainingSeconds--;
                continue;
            }

            if (state.mode == Mode.STUDY) {
                state.mode = Mode.BREAK;
                state.remainingSeconds = state.breakSeconds;
            } else if (state.mode == Mode.BREAK) {
                if (state.activeTodoId != null) {
                    todoService.completeTodo(state.user, state.activeTodoId);
                }
                state.mode = Mode.IDLE;
                state.remainingSeconds = 0;
            }
        }
    }
}
