package com.example.ecommerce.security;

import com.example.ecommerce.config.JwtProperties;
import com.example.ecommerce.user.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

/**
 * Access token cookie işlemleri.
 * <p>
 * Güvenlik ayarları:
 * HttpOnly  → JavaScript erişemez (XSS koruması)
 * Secure    → Yalnızca HTTPS (prod'da zorunlu)
 * SameSite=Strict → Aynı domain; CSRF imkânsız
 * Path=/api → Cookie yalnızca API isteklerinde gider
 */
@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "app.security.cookie")
public class JwtCookieUtil {

    private String accessTokenName;
    private String refreshTokenName;
    private String accessTokenPath;
    private String refreshTokenPath;

    private final JwtProperties jwtProperties;

    public void addAuthCookies(HttpServletResponse response,
                               String accessToken,
                               String compositeRefreshToken) {
        addAccessCookie(response, accessToken);
        addRefreshCookie(response, compositeRefreshToken);
    }

    public void clearAuthCookies(HttpServletResponse response) {
        clearCookie(response, accessTokenName, accessTokenPath);
        clearCookie(response, refreshTokenName, refreshTokenPath);
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return extractCookie(request, accessTokenName);
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return extractCookie(request, refreshTokenName);
    }

    // helpers

    private void addAccessCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(accessTokenName, token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path(accessTokenPath)
                .maxAge(jwtProperties.getAccessTokenExpiration())
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void addRefreshCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(refreshTokenName, token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path(refreshTokenPath)
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