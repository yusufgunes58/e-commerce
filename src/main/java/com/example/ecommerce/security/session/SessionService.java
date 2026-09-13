package com.example.ecommerce.security.session;

import com.example.ecommerce.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionStore sessionStore;
    private final JwtProperties jwtProperties;

    private final SecureRandom secureRandom = new SecureRandom();


    public String createSession(Long userId) {

        String sessionId = UUID.randomUUID().toString();

        String refreshToken = generateRefreshToken();

        Instant now = Instant.now();

        Instant absoluteExpiresAt = now.plus(jwtProperties.getMaxSessionDuration(), ChronoUnit.SECONDS);

        long remainingSeconds = now.until(absoluteExpiresAt, ChronoUnit.SECONDS);

        long ttl = Math.min(jwtProperties.getRefreshTokenExpiration(), remainingSeconds);

        if (ttl <= 0) {
            throw new IllegalStateException("Invalid session expiration configuration");
        }

        String refreshTokenHash = hashToken(refreshToken);

        sessionStore.save(sessionId, userId, refreshTokenHash, now, absoluteExpiresAt, ttl);

        return sessionId + "." + refreshToken;
    }


    public RotationResult rotateSession(String compositeToken) {

        String[] parts = splitComposite(compositeToken);

        if (parts == null) {
            return null;
        }

        String sessionId = parts[0];
        String oldRefreshToken = parts[1];

        SessionData session = sessionStore.find(sessionId);

        if (session == null) {

            log.warn("Session not found: {}", sessionId);

            return null;
        }

        Instant now = Instant.now();

        if (!now.isBefore(session.getAbsoluteExpiresAt())) {

            sessionStore.delete(sessionId);

            log.info("Session absolute expiry reached: {}", sessionId);

            return null;
        }

        String oldTokenHash = hashToken(oldRefreshToken);

        String newRefreshToken = generateRefreshToken();
        String newTokenHash = hashToken(newRefreshToken);

        long remainingSeconds =
                now.until(session.getAbsoluteExpiresAt(), ChronoUnit.SECONDS);

        long ttl = Math.min(
                jwtProperties.getRefreshTokenExpiration(),
                remainingSeconds
        );

        if (ttl <= 0) {

            sessionStore.delete(sessionId);

            return null;
        }

        boolean rotated = sessionStore.rotate(
                sessionId,
                oldTokenHash,
                newTokenHash,
                ttl
        );

        if (!rotated) {

            log.warn("Refresh token mismatch: {}", sessionId);

            /*
             * Token theft ihtimali nedeniyle
             * session tamamen iptal ediliyor.
             */
            sessionStore.delete(sessionId);

            return null;
        }

        String newCompositeToken =
                sessionId + "." + newRefreshToken;

        return new RotationResult(
                session.getUserId(),
                newCompositeToken
        );
    }


    public boolean sessionExists(String sessionId) {

        return sessionStore.exists(sessionId);
    }


    public void deleteSession(String sessionId) {

        sessionStore.delete(sessionId);

        log.info("Session deleted: {}", sessionId);
    }


    private String generateRefreshToken() {

        byte[] randomBytes = new byte[32];

        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }


    private String hashToken(String token) {

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hash);

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException("SHA-256 algorithm is not available", e);
        }
    }


    private String[] splitComposite(String compositeToken) {

        if (compositeToken == null) {
            return null;
        }

        int dot = compositeToken.indexOf('.');

        if (dot < 1 || dot == compositeToken.length() - 1) {

            return null;
        }

        return new String[]{compositeToken.substring(0, dot), compositeToken.substring(dot + 1)};
    }

    public record RotationResult(
            Long userId,
            String compositeRefreshToken
    ) {}
}
