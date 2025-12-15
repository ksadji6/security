package com.misi2.springsecuritydemo.config;

import com.misi2.springsecuritydemo.model.Role;
import com.misi2.springsecuritydemo.model.User;
import com.misi2.springsecuritydemo.service.TaskService;
import com.misi2.springsecuritydemo.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserService userService, TaskService taskService) {
        return args -> {
            // Create Users
            if (userService.findAllUsers().isEmpty()) {
                User admin = userService.createUser("admin", "password", Role.ROLE_ADMIN);
                User gerant = userService.createUser("gerant", "password", Role.ROLE_GERANT);
                User user1 = userService.createUser("user", "password", Role.ROLE_USER);
                User user2 = userService.createUser("user2", "password", Role.ROLE_USER);

                // Create Tasks
                taskService.createTask("System Update", "Update server OS", admin);
                taskService.createTask("Review Reports", "Monthly performance review", gerant);
                taskService.createTask("Fix Bug #101", "Fix NPE in login", user1);
                taskService.createTask("Write Documentation", "Update API docs", user1);
                taskService.createTask("Client Meeting", "Discuss requirements", user2);
            }
        };
    }
}
