package com.example.bankcards.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(
        @NotNull @Valid Long fromId,
        @NotNull @Valid Long toId,
        @NotNull @Valid @Min(0) BigDecimal amount
) {
}
