package com.example.bankcards.entity;

import com.example.bankcards.util.CardNumberEncryptor;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "cards")
@Data
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private User user;

    @Convert(converter = CardNumberEncryptor.class)
    @Column(name = "number", nullable = false, columnDefinition = "bytea")
    private String number;

    @Column(name = "las4", nullable = false)
    private String last4;

    @Column(name = "cardholder_name", nullable = false)
    private String cardholderName;

    @Column(name = "expiration_month", nullable = false)
    private Short expirationMonth;

    @Column(name = "expiration_year", nullable = false)
    private Short expirationYear;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @Column(name = "balance", precision = 18, scale = 2, nullable = false)
    private BigDecimal balance;

}
