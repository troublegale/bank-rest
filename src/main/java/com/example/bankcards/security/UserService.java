package com.example.bankcards.security;

import com.example.bankcards.dto.RegistrationRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EmailTakenException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.RoleService;
import com.example.bankcards.util.ModelDTOConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepo;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByEmail(username);
    }

    @Transactional
    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format("User %s not found", email)));
    }

    @Transactional
    public Boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Transactional
    public User createUser(RegistrationRequest request) {
        String email = request.email();
        if (userRepo.existsByEmail(email)) {
            throw new EmailTakenException(String.format("Email %s is already taken", email));
        }
        Role userRole = roleService.getUserRole();
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .roles(List.of(userRole))
                .build();
        return userRepo.save(user);
    }

    @Transactional
    public void createUser(User user) {
        userRepo.save(user);
    }

    @Transactional
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByEmail(email);
    }

}
