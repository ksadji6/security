package com.misi2.springsecuritydemo.service;

import com.misi2.springsecuritydemo.model.Status;
import com.misi2.springsecuritydemo.model.Task;
import com.misi2.springsecuritydemo.model.User;
import com.misi2.springsecuritydemo.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTasksForUser(User user) {
        return taskRepository.findByAssignedTo(user);
    }

    public void createTask(String title, String description, User assignedTo) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(Status.TODO);
        task.setAssignedTo(assignedTo);
        taskRepository.save(task);
    }

    public void updateTask(Long id, String title, String description, String status, User assignedTo) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        if (title != null)
            task.setTitle(title);
        if (description != null)
            task.setDescription(description);
        if (status != null)
            task.setStatus(Status.valueOf(status));
        if (assignedTo != null)
            task.setAssignedTo(assignedTo);
        taskRepository.save(task);
    }

    public void updateTaskStatus(Long id, String status) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(Status.valueOf(status));
        taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
    }
}
