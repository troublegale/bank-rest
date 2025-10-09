package com.example.bankcards.dto;

public record TokenResponse(
        String jwtToken,
        String refreshToken
) {
}
