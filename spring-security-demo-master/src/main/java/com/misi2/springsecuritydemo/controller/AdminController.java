package com.misi2.springsecuritydemo.controller;

import com.misi2.springsecuritydemo.model.Role;
import com.misi2.springsecuritydemo.service.TaskService;
import com.misi2.springsecuritydemo.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final TaskService taskService;

    public AdminController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("roles", Role.values());
        return "admin/users";
    }

    @PostMapping("/users")
    public String createUser(@RequestParam String username, @RequestParam String password, @RequestParam Role role) {
        userService.createUser(username, password, role);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/edit")
    public String editUserRole(@PathVariable Long id, @RequestParam Role role) {
        userService.updateUserRole(id, role);
        return "redirect:/admin/users";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("userCount", userService.findAllUsers().size());
        model.addAttribute("taskCount", taskService.getAllTasks().size());
        return "admin/dashboard";
    }
}
