package com.studyfocus.service;

import org.springframework.stereotype.Component;

@Component
public class TodoTimeTracker {

    private final TodoService todoService;

    public TodoTimeTracker(TodoService todoService) {
        this.todoService = todoService;
    }

    public void trackStudy() {
        todoService.addStudySecond();
    }

    public void trackBreak() {
        todoService.addBreakSecond();
    }
}
