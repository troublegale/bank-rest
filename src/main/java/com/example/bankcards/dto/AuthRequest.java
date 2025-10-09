package com.example.bankcards.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AuthRequest(
        @Valid @NotNull String email,
        @Valid @NotNull String password
) {
}
