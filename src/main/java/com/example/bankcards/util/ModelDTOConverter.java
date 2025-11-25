package com.example.bankcards.util;

import com.example.bankcards.dto.CardRequest;
import com.example.bankcards.dto.CardResponse;
import com.example.bankcards.dto.UserResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;

import java.math.BigDecimal;
import java.util.List;

public class ModelDTOConverter {

    public static UserResponse convert(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles().stream().map(Role::getName).toList()
        );
    }

    public static List<UserResponse> toUserResponseList(List<User> users) {
        return users.stream().map(ModelDTOConverter::convert).toList();
    }

    public static CardResponse convert(Card card) {
        return new CardResponse(
                card.getId(),
                card.getUser().getId(),
                "**** **** **** " + card.getLast4(),
                card.getCardholderName(),
                card.getExpirationMonth(),
                card.getExpirationYear(),
                card.getStatus()
        );
    }

    public static List<CardResponse> toCardResponseList(List<Card> cards) {
        return cards.stream().map(ModelDTOConverter::convert).toList();
    }

    public static Card convert(CardRequest cardRequest) {
        return Card.builder()
                .number(cardRequest.number())
                .last4(cardRequest.number().substring(12))
                .cardholderName(cardRequest.cardholderName().toUpperCase())
                .expirationMonth(cardRequest.expirationMonth())
                .expirationYear(cardRequest.expirationYear())
                .status(CardStatus.CREATED)
                .balance(new BigDecimal(0))
                .build();
    }

}
