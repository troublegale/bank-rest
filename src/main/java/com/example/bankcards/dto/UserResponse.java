package com.example.bankcards.dto;


import com.example.bankcards.entity.Role;

import java.util.List;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        List<Role> roles
) {
}
