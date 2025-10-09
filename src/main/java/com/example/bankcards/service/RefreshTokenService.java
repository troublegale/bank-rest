package com.example.bankcards.service;

import com.example.bankcards.entity.RefreshToken;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.InvalidRefreshTokenException;
import com.example.bankcards.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepo;

    @Value("${jwt.refresh-lifetime}")
    private Duration lifetime;

    @Transactional
    public String getRefreshToken(User user) {
        Optional<RefreshToken> existingToken = refreshTokenRepo.findByUser(user);
        if (existingToken.isPresent()) {
            return existingToken.get().getToken().toString();
        }
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID())
                .expiresAt(new Date(System.currentTimeMillis() + lifetime.toMillis()))
                .build();
        refreshTokenRepo.save(refreshToken);
        return refreshToken.getToken().toString();
    }

    @Transactional
    public User getUserByRefreshToken(UUID uuidToken) {
        RefreshToken token = refreshTokenRepo.findByToken(uuidToken).orElseThrow(InvalidRefreshTokenException::new);
        if (new Date().after(token.getExpiresAt())) {
            refreshTokenRepo.delete(token);
            throw new InvalidRefreshTokenException();
        }
        return token.getUser();
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepo.deleteByUser(user);
    }

}
