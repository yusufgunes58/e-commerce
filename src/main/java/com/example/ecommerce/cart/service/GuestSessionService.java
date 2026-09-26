package com.example.ecommerce.cart.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GuestSessionService {

    private static final String COOKIE_NAME = "guest_session_id";
    private static final Duration COOKIE_MAX_AGE =Duration.ofDays(7);

    public String getOrCreateGuestSessionId(
                String sessionId,
                HttpServletResponse response
    ) {
        if(sessionId != null && !sessionId.isBlank()) {
            return sessionId;
        }

        String newSessionId = UUID.randomUUID().toString();

        ResponseCookie cookie = ResponseCookie
                .from(COOKIE_NAME, newSessionId)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/v1/cart/guest")
                .maxAge(COOKIE_MAX_AGE)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return  newSessionId;
    }



}
