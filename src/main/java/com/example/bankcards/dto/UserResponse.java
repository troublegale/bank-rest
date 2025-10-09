package com.example.bankcards.dto;


import java.util.List;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        List<String> roles
) {
}
