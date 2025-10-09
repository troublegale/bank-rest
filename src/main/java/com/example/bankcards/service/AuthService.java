package com.example.bankcards.service;

import com.example.bankcards.dto.*;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.InvalidRefreshTokenException;
import com.example.bankcards.security.JwtManager;
import com.example.bankcards.security.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtManager jwtManager;

    @Transactional
    public TokenResponse register(RegistrationRequest registrationRequest) {
        User user = userService.createUser(registrationRequest);
        String jwt = jwtManager.generateToken(user);
        String refreshToken = refreshTokenService.getRefreshToken(user);
        return new TokenResponse(jwt, refreshToken);
    }

    @Transactional
    public TokenResponse login(AuthRequest authRequest) {
        String email = authRequest.email();
        String password = authRequest.password();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        User user = userService.findByEmail(email);
        String jwt = jwtManager.generateToken(user);
        String refreshToken = refreshTokenService.getRefreshToken(user);
        return new TokenResponse(jwt, refreshToken);
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest refreshRequest) {
        UUID uuidToken;
        try {
            uuidToken = UUID.fromString(refreshRequest.refreshToken());
        } catch (Exception e) {
            throw new InvalidRefreshTokenException();
        }
        User user = refreshTokenService.getUserByRefreshToken(uuidToken);
        String jwt = jwtManager.generateToken(user);
        String refreshToken = uuidToken.toString();
        return new TokenResponse(jwt, refreshToken);
    }

    @Transactional
    public void logout() {
        User user = userService.getCurrentUser();
        refreshTokenService.deleteByUser(user);
    }

    @Transactional
    public UserResponse getCurrentUserInfo() {
        User user = userService.getCurrentUser();
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles().stream().toList()
        );
    }

}
