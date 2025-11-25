package com.example.bankcards.dto;

import java.math.BigDecimal;

public record BalanceResponse(
        String number,
        BigDecimal balance
) {
}
