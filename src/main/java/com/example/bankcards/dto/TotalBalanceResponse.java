package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.util.List;

public record TotalBalanceResponse(
        List<BalanceResponse> cards,
        BigDecimal totalBalance
) {
}
