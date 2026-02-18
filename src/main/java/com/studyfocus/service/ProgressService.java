//package com.studyfocus.service;
//
//import com.studyfocus.model.Progress;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Service
//public class ProgressService {
//
//    private final Map<LocalDate, Progress> progressMap = new ConcurrentHashMap<>();
//
//    private Progress today() {
//        return progressMap.computeIfAbsent(LocalDate.now(), d -> new Progress());
//    }
//
//    public void addStudyTime(long sec) {
//        today().addStudyTime(sec);
//    }
//
//    public void addBreakTime(long sec) {
//        today().addBreakTime(sec);
//    }
//
//    public void addIdleTime(long sec) {
//        today().addIdleTime(sec);
//    }
//
//    public void alarmTriggered() {
//        today().incAlarms();
//    }
//
//    public void sessionCompleted() {
//        today().incSessions();
//    }
//
//    public Map<LocalDate, Progress> getAll() {
//        return progressMap;
//    }
//}

package com.studyfocus.service;

import com.studyfocus.model.Progress;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProgressService {

    private final List<Progress> records = new ArrayList<>();

    public void add(String state) {
        records.add(new Progress(state, LocalDateTime.now()));
    }

    public List<Progress> getAll() {
        return records;
    }
}
