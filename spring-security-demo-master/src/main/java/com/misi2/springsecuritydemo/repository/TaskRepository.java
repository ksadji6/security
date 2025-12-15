package com.misi2.springsecuritydemo.repository;

import com.misi2.springsecuritydemo.model.Task;
import com.misi2.springsecuritydemo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedTo(User user);
}
