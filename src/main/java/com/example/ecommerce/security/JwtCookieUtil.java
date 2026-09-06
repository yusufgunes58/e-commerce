package com.example.ecommerce.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
public class JwtCookieUtil {

    static final String ACCESS_TOKEN_COOKIE = "access_token";

    /*
     * Access token'ı HttpOnly cookie olarak response'a yazar.
     * maxAge = access token süresi (saniye).
     */
    public void addAccessTokenCookie(HttpServletResponse response,
                                     String accessToken,
                                     long expiresInSeconds) {
        ResponseCookie cookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE, accessToken)
                .httpOnly(true)
                .secure(false) // http://localhost then must change it to TRUE.
                .sameSite("Lax")  // its Lax for  payment API.
                .path("/api")
                .maxAge(expiresInSeconds)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    /**
     * Cookie'yi sıfırlar (logout).
     * maxAge=0 → tarayıcı cookie'yi hemen siler.
     */
    public void clearAccessTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    /** Request cookie'lerinden access token'ı çıkarır. */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        if(request.getCookies() == null) {return Optional.empty(); }

        return Arrays.stream(request.getCookies())
                .filter(c-> ACCESS_TOKEN_COOKIE.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

}
