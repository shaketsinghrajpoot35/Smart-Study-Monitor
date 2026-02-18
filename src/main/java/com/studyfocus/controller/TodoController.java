package com.studyfocus.controller;

import com.studyfocus.entity.TodoTask;
import com.studyfocus.service.TodoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todo")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // ✅ ADD TODO
    @PostMapping
    public TodoTask addTodo(@RequestBody TodoTask todo) {
        return todoService.addTodo(todo);
    }

    // ✅ GET ALL TODOS
    @GetMapping
    public List<TodoTask> getTodos() {
        return todoService.getAllTodos();
    }

    // ▶ START TODO
    @PostMapping("/{id}/start")
    public void startTodo(@PathVariable Long id) {
        todoService.startTodo(id);
    }

    // ✅ COMPLETE TODO
    @PostMapping("/{id}/complete")
    public void completeTodo(@PathVariable Long id) {
        todoService.completeTodo(id);
    }

    // ❌ DELETE TODO
    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
    }
}
