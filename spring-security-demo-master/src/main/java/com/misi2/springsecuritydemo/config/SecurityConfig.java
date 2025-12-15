package com.misi2.springsecuritydemo.config;

import com.misi2.springsecuritydemo.repository.UserRepository;
import com.misi2.springsecuritydemo.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public SecurityConfig(UserService userService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {

        http.csrf(AbstractHttpConfigurer::disable); // For demo simplicity
        http
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/admin/**").hasRole("ADMIN")
                            .requestMatchers("/gerant/**").hasRole("GERANT")
                            .requestMatchers("/tasks/**").authenticated()
                            .requestMatchers("/", "/login", "/css/**", "/js/**").permitAll()
                            .anyRequest().authenticated();
                })
                // Login avec formulaire
                .formLogin(form -> {
                    form
                            .loginPage("/login")
                            .successHandler(new CustomAuthenticationSuccessHandler())
                            .permitAll();
                })
                // Login avec OAuth2
                .oauth2Login(oauth2 -> {
                    oauth2
                            .loginPage("/login")
                            .userInfoEndpoint(userInfo -> userInfo
                                    .userService(new CustomOAuth2UserService(userRepository)))
                            .successHandler(new CustomAuthenticationSuccessHandler());
                })
                // Logout
                .logout(logout -> {
                    logout
                            .logoutSuccessUrl("/login?logout")
                            .permitAll();
                });

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}