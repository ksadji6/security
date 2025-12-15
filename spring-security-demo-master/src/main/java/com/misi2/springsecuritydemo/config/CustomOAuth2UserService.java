package com.misi2.springsecuritydemo.config;

import com.misi2.springsecuritydemo.model.Role;
import com.misi2.springsecuritydemo.model.User;
import com.misi2.springsecuritydemo.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        // Extract user info from OAuth2 provider
        String provider = userRequest.getClientRegistration().getRegistrationId(); // "google"
        String providerId = oauth2User.getAttribute("sub"); // Google user ID
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        // Find or create user
        User user = userRepository.findByUsername(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(email);
                    newUser.setPassword(""); // No password for OAuth2 users
                    newUser.setRole(Role.ROLE_USER); // Default role for OAuth2 users
                    newUser.setProvider(provider);
                    newUser.setProviderId(providerId);
                    return userRepository.save(newUser);
                });

        // Update provider info if user exists but was created via form login
        if (user.getProvider() == null) {
            user.setProvider(provider);
            user.setProviderId(providerId);
            userRepository.save(user);
        }

        // Return CustomUserDetails wrapping the user
        return new CustomUserDetails(user, oauth2User.getAttributes());
    }
}
