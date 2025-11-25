package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;

public record CardResponse(
        Long id,
        Long userId,
        String number,
        String cardholderName,
        Short expirationMonth,
        Short expirationYear,
        CardStatus status
) {
}
