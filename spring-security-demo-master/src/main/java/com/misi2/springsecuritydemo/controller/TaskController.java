package com.misi2.springsecuritydemo.controller;

import com.misi2.springsecuritydemo.config.CustomUserDetails;
import com.misi2.springsecuritydemo.model.Role;
import com.misi2.springsecuritydemo.model.Task;
import com.misi2.springsecuritydemo.model.User;
import com.misi2.springsecuritydemo.service.TaskService;
import com.misi2.springsecuritydemo.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping
    public String listTasks(Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long assignee) {
        User user = userDetails.getUser();
        List<Task> tasks;

        // Get tasks based on role
        if (user.getRole() == Role.ROLE_ADMIN || user.getRole() == Role.ROLE_GERANT) {
            tasks = taskService.getAllTasks();
        } else {
            tasks = taskService.getTasksForUser(user);
        }

        // Apply filters
        if (status != null && !status.isEmpty()) {
            tasks = tasks.stream()
                    .filter(task -> task.getStatus().name().equals(status))
                    .toList();
        }

        if (assignee != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getAssignedTo() != null && task.getAssignedTo().getId().equals(assignee))
                    .toList();
        }

        // Sort: User's own tasks first, then others
        if (user.getRole() == Role.ROLE_ADMIN || user.getRole() == Role.ROLE_GERANT) {
            tasks = tasks.stream()
                    .sorted((t1, t2) -> {
                        boolean t1IsOwn = t1.getAssignedTo() != null && t1.getAssignedTo().getId().equals(user.getId());
                        boolean t2IsOwn = t2.getAssignedTo() != null && t2.getAssignedTo().getId().equals(user.getId());

                        if (t1IsOwn && !t2IsOwn)
                            return -1; // t1 first
                        if (!t1IsOwn && t2IsOwn)
                            return 1; // t2 first
                        return 0; // Keep original order
                    })
                    .toList();
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("currentUser", user);
        model.addAttribute("users", userService.findAllUsers()); // For assigning tasks
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedAssignee", assignee);
        return "tasks/list";
    }

    @PostMapping
    public String createTask(@RequestParam String title, @RequestParam String description,
            @RequestParam(required = false) Long assignedToId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        if (user.getRole() == Role.ROLE_USER) {
            return "redirect:/tasks?error=unauthorized";
        }

        User assignedUser = null;
        if (assignedToId != null) {
            assignedUser = userService.findById(assignedToId);
        }

        taskService.createTask(title, description, assignedUser);
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        Task task = taskService.getTaskById(id);

        // Users can only update status of their own tasks
        if (user.getRole() == Role.ROLE_USER && !task.getAssignedTo().getId().equals(user.getId())) {
            return "redirect:/tasks?error=unauthorized";
        }

        taskService.updateTaskStatus(id, status);
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/reassign")
    public String reassignTask(@PathVariable Long id, @RequestParam Long assignedToId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        // Only ADMIN and GERANT can reassign tasks
        if (user.getRole() == Role.ROLE_USER) {
            return "redirect:/tasks?error=unauthorized";
        }

        User newAssignee = userService.findById(assignedToId);
        taskService.updateTask(id, null, null, null, newAssignee);
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        if (user.getRole() == Role.ROLE_ADMIN) {
            taskService.deleteTask(id);
        }
        return "redirect:/tasks";
    }
}
