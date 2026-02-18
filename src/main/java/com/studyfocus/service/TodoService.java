package com.studyfocus.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studyfocus.entity.TodoTask;
import com.studyfocus.model.TodoStatus;
import com.studyfocus.repository.TodoRepository;

@Service
@Transactional
public class TodoService {

    private final TodoRepository repo;

    public TodoService(TodoRepository repo) {
        this.repo = repo;
    }

    /* =================================================
       ADD TODO
    ================================================= */
    public TodoTask addTodo(TodoTask todo) {
        todo.setStatus(TodoStatus.PENDING);
        todo.setActive(false);
        todo.setActualStudySeconds(0);
        todo.setActualBreakSeconds(0);
        return repo.save(todo);
    }

    /* =================================================
       GET ALL TODOS
    ================================================= */
    public List<TodoTask> getAllTodos() {
        return repo.findAll();
    }

    /* =================================================
       DELETE TODO
    ================================================= */
    public void deleteTodo(Long id) {
        repo.deleteById(id);
    }

    /* =================================================
       ACTIVE TODO
    ================================================= */
    public Optional<TodoTask> getActiveTodo() {
        return repo.findByActiveTrue();
    }

    /* =================================================
       START TODO
    ================================================= */
    public void startTodo(Long id) {

        // ⏸ Pause any currently active todo
        repo.findByActiveTrue().ifPresent(active -> {
            active.setActive(false);
            active.setStatus(TodoStatus.PAUSED);
            repo.save(active);
        });

        // ▶ Start selected todo
        TodoTask todo = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        todo.setActive(true);
        todo.setStatus(TodoStatus.IN_PROGRESS);
        repo.save(todo);
    }

    /* =================================================
       COMPLETE TODO
    ================================================= */
    public void completeTodo(Long id) {
        TodoTask todo = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        todo.setActive(false);
        todo.setStatus(TodoStatus.COMPLETED);
        repo.save(todo);
    }

    /* =================================================
       PAUSE ACTIVE TODO
    ================================================= */
    public void pauseActiveTodo() {
        repo.findByActiveTrue().ifPresent(todo -> {
            todo.setActive(false);
            todo.setStatus(TodoStatus.PAUSED);
            repo.save(todo);
        });
    }

    /* =================================================
       TIME TRACKING (CALLED BY TIMER)
    ================================================= */
    public void addStudySecond() {
        repo.findByActiveTrue().ifPresent(todo -> {
            todo.setActualStudySeconds(
                todo.getActualStudySeconds() + 1
            );
            repo.save(todo);
        });
    }

    public void addBreakSecond() {
        repo.findByActiveTrue().ifPresent(todo -> {
            todo.setActualBreakSeconds(
                todo.getActualBreakSeconds() + 1
            );
            repo.save(todo);
        });
    }
}
