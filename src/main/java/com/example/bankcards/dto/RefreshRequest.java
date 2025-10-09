package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UUID;

public record RefreshRequest(@NotNull @UUID String refreshToken){
}
