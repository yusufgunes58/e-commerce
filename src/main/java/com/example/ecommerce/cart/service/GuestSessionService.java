package com.example.ecommerce.cart.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "app.guest-cart")
public class GuestSessionService {

    private String cookieName;
    private long cookieMaxAgeDays;

    public String getOrCreateGuestSession(
                String sessionId,
                HttpServletResponse response
    ) {
        if(sessionId != null && !sessionId.isBlank()) {
            return sessionId;
        }

        String newSessionId = UUID.randomUUID().toString();

        ResponseCookie cookie = ResponseCookie
                .from(cookieName, newSessionId)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api")
                .maxAge(cookieMaxAgeDays)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return  newSessionId;
    }



}
