package com.example.bankcards.config;

import com.example.bankcards.entity.User;
import com.example.bankcards.security.UserService;
import com.example.bankcards.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultAdminLoader {

    @Value("${credentials.admin.email}")
    private String adminEmail;

    @Value("${credentials.admin.password}")
    private String adminPassword;

    @Value("${credentials.admin.first-name}")
    private String adminFirstName;

    @Value("${credentials.admin.last-name}")
    private String adminLastName;

    private final PasswordEncoder passwordEncoder;

    private final UserService userService;
    private final RoleService roleService;

    @EventListener(ApplicationReadyEvent.class)
    public void loadDefaultAdmin() {
        if (!userService.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .firstName(adminFirstName)
                    .lastName(adminLastName)
                    .roles(List.of(roleService.getAdminRole()))
                    .build();
            userService.createUser(admin);
        }
    }

}
