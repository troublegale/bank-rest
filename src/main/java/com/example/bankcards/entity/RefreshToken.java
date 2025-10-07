package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Data
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private User user;

    @Column(name = "token", nullable = false, unique = true)
    private UUID token;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

}
