package com.misi2.springsecuritydemo.config;

import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    @NullMarked
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        // Redirect based on user role
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();

            switch (role) {
                case "ROLE_ADMIN" -> {
                    response.sendRedirect("/admin/dashboard");
                    return;
                }
                case "ROLE_GERANT", "ROLE_USER" -> {
                    response.sendRedirect("/tasks");
                    return;
                }
                case null -> {
                    response.sendRedirect("/login?error=true");
                    return;
                }
                default -> throw new IllegalStateException("Unexpected value: " + role);
            }
        }

        // Default redirect
        response.sendRedirect("/");
    }
}
