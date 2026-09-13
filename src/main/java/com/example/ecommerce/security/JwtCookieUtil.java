package com.example.ecommerce.security;

import com.example.ecommerce.config.JwtProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

/**
 * Access token cookie işlemleri.
 *
 * Güvenlik ayarları:
 *   HttpOnly  → JavaScript erişemez (XSS koruması)
 *   Secure    → Yalnızca HTTPS (prod'da zorunlu)
 *   SameSite=Strict → Aynı domain; CSRF imkânsız
 *   Path=/api → Cookie yalnızca API isteklerinde gider
 */
@Component
@RequiredArgsConstructor
public class JwtCookieUtil {

    private static final String ACCESS_TOKEN_COOKIE  = "access_token";
    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private final JwtProperties jwtProperties;

    // Login / Register — her iki cookie yazılır
    public void addAuthCookies(HttpServletResponse response,
                               String accessToken,
                               String compositeRefreshToken) {
        addAccessCookie(response, accessToken);
        addRefreshCookie(response, compositeRefreshToken);
    }

    // Logout — her iki cookie temizlenir
    public void clearAuthCookies(HttpServletResponse response) {
        clearCookie(response, ACCESS_TOKEN_COOKIE,  "/api");
        clearCookie(response, REFRESH_TOKEN_COOKIE, "/api/v1/auth/refresh");
    }

    // Okuma
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return extractCookie(request, ACCESS_TOKEN_COOKIE);
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return extractCookie(request, REFRESH_TOKEN_COOKIE);
    }

    // Private helpers
    private void addAccessCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api")
                .maxAge(jwtProperties.getAccessTokenExpiration())
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void addRefreshCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/v1/auth/refresh")
                .maxAge(jwtProperties.getRefreshTokenExpiration())
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void clearCookie(HttpServletResponse response, String name, String path) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path(path)
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private Optional<String> extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return Optional.empty();
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

}
