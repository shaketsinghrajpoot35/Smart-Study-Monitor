package com.studyfocus.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studyfocus.entity.TodoTask;

public interface TodoRepository extends JpaRepository<TodoTask, Long> {

    // ✅ FIND CURRENT ACTIVE TODO
    Optional<TodoTask> findByActiveTrue();
}
