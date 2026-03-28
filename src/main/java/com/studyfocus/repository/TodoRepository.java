package com.studyfocus.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.studyfocus.entity.TodoTask;
import com.studyfocus.entity.User;

public interface TodoRepository extends JpaRepository<TodoTask, Long> {
    Optional<TodoTask> findByUserAndActiveTrue(User user);
    List<TodoTask> findByUser(User user);
    Optional<TodoTask> findByIdAndUser(Long id, User user);
}
