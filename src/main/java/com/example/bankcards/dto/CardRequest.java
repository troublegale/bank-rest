package com.example.bankcards.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record CardRequest(
        @NotNull @Valid Long userId,
        @NotNull @Valid @Size(min = 16, max = 16) @Pattern(regexp = "\\d{16}") String number,
        @NotNull @NotBlank @Valid @Size(max = 128) String cardholderName,
        @NotNull @Valid @Min(1) @Max(12) Short expirationMonth,
        @NotNull @Valid @Min(2000) @Max(2099) Short expirationYear
) {
}
