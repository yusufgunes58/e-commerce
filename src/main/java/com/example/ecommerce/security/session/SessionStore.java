package com.example.ecommerce.security.session;

import java.time.Instant;

public interface SessionStore {


    void save(
            String sessionId,
            Long userId,
            String refreshTokenHash,
            Instant createdAt,
            Instant absoluteExpiresAt,
            long ttlSeconds
    );

    SessionData find(String sessionId);

    boolean rotate(
            String sessionId,
            String oldTokenHash,
            String newTokenHash,
            long ttlSeconds
    );

    void delete(String sessionId);

    boolean exists(String sessionId);

}
