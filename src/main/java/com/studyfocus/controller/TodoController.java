package com.studyfocus.controller;

import com.studyfocus.entity.TodoTask;
import com.studyfocus.entity.User;
import com.studyfocus.repository.UserRepository;
import com.studyfocus.service.TodoService;
import com.studyfocus.service.TimerService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/todo")
public class TodoController {

    private final TodoService todoService;
    private final UserRepository userRepository;
    private final TimerService timerService;

    public TodoController(TodoService todoService, UserRepository userRepository, TimerService timerService) {
        this.todoService = todoService;
        this.userRepository = userRepository;
        this.timerService = timerService;
    }

    private User getUser(Authentication auth) {
        if (auth == null) return null;
        return userRepository.findByUsername(auth.getName()).orElse(null);
    }

    @PostMapping
    public TodoTask addTodo(Authentication auth, @RequestBody TodoTask todo) {
        User user = getUser(auth);
        if (user == null) return null;
        return todoService.addTodo(user, todo);
    }

    @GetMapping
    public List<TodoTask> getTodos(Authentication auth) {
        User user = getUser(auth);
        if (user == null) return Collections.emptyList();
        return todoService.getAllTodos(user);
    }

    @PostMapping("/{id}/start")
    public void startTodo(Authentication auth, @PathVariable Long id) {
        User user = getUser(auth);
        if (user == null) return;
        todoService.startTodo(user, id);

        // Direct single-fetch — avoids loading all todos just to find one
        todoService.getTodoById(user, id).ifPresent(task ->
            timerService.startForTodo(user, id, task.getPlannedStudyMinutes(), task.getPlannedBreakMinutes())
        );
    }

    @PostMapping("/{id}/complete")
    public void completeTodo(Authentication auth, @PathVariable Long id) {
        User user = getUser(auth);
        if (user != null) todoService.completeTodo(user, id);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(Authentication auth, @PathVariable Long id) {
        User user = getUser(auth);
        if (user != null) todoService.deleteTodo(user, id);
    }
}
