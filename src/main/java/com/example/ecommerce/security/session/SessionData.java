package com.example.ecommerce.security.session;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class SessionData {

    private Long userId;
    private String refreshTokenHash;
    private Instant createdAt;
    private Instant absoluteExpiresAt;
}