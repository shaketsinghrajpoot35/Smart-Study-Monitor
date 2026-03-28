package com.studyfocus.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studyfocus.entity.TodoTask;
import com.studyfocus.entity.User;
import com.studyfocus.model.TodoStatus;
import com.studyfocus.repository.TodoRepository;

@Service
@Transactional
public class TodoService {

    private final TodoRepository repo;

    public TodoService(TodoRepository repo) {
        this.repo = repo;
    }

    public TodoTask addTodo(User user, TodoTask todo) {
        if (user == null) throw new RuntimeException("User required");
        todo.setUser(user);
        todo.setStatus(TodoStatus.PENDING);
        todo.setActive(false);
        todo.setActualStudySeconds(0);
        todo.setActualBreakSeconds(0);
        return repo.save(todo);
    }

    public List<TodoTask> getAllTodos(User user) {
        return repo.findByUser(user);
    }

    public Optional<TodoTask> getTodoById(User user, Long id) {
        return repo.findByIdAndUser(id, user);
    }

    public void deleteTodo(User user, Long id) {
        repo.findByIdAndUser(id, user).ifPresent(repo::delete);
    }

    public Optional<TodoTask> getActiveTodo(User user) {
        return repo.findByUserAndActiveTrue(user);
    }

    public void startTodo(User user, Long id) {
        repo.findByUserAndActiveTrue(user).ifPresent(active -> {
            active.setActive(false);
            active.setStatus(TodoStatus.PAUSED);
            repo.save(active);
        });

        TodoTask todo = repo.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        todo.setActive(true);
        todo.setStatus(TodoStatus.IN_PROGRESS);
        repo.save(todo);
    }

    public void completeTodo(User user, Long id) {
        if (id == null) {
            getActiveTodo(user).ifPresent(todo -> {
                todo.setActive(false);
                todo.setStatus(TodoStatus.COMPLETED);
                repo.save(todo);
            });
            return;
        }

        TodoTask todo = repo.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        todo.setActive(false);
        todo.setStatus(TodoStatus.COMPLETED);
        repo.save(todo);
    }

    public void pauseActiveTodo(User user) {
        repo.findByUserAndActiveTrue(user).ifPresent(todo -> {
            todo.setActive(false);
            todo.setStatus(TodoStatus.PAUSED);
            repo.save(todo);
        });
    }

    public void addStudySeconds(User user, int seconds) {
        repo.findByUserAndActiveTrue(user).ifPresent(todo -> {
            todo.setActualStudySeconds(todo.getActualStudySeconds() + seconds);
            repo.save(todo);
        });
    }

    public void addBreakSeconds(User user, int seconds) {
        repo.findByUserAndActiveTrue(user).ifPresent(todo -> {
            todo.setActualBreakSeconds(todo.getActualBreakSeconds() + seconds);
            repo.save(todo);
        });
    }
}
