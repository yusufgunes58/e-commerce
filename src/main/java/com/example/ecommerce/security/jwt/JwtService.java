package com.example.ecommerce.security.jwt;

import com.example.ecommerce.config.JwtProperties;

import com.example.ecommerce.user.entity.CustomUserDetails;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import io.jsonwebtoken.Claims;
import java.time.Instant;
import java.util.Date;

/**
 * JWT üretimi ve doğrulaması.
 * Session yönetimi SessionService'e aittir; bu sınıf yalnızca token işler.
 */
@Service
@Slf4j
public class JwtService {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "ACCESS";

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        String secret = jwtProperties.getSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret is not configured.");
        }
        byte[] keyBytes = Decoders.BASE64.decode(secret);

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // Token Generation
    public String generateToken(CustomUserDetails customUserDetails) {
        String role = customUserDetails.getAuthorities().iterator().next().getAuthority();

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(String.valueOf(customUserDetails.getId()))
                .claim(CLAIM_ROLE, role)
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                .issuedAt(Date.from(now))
                .expiration(Date.from(
                        now.plusSeconds(
                                jwtProperties.getAccessTokenExpiration()
                        )
                ))
                .signWith(signingKey)
                .compact();
    }

    // Token doğrulama
    public Claims parseAccessToken(String token) {
        Claims claims = parseAllClaims(token);

        if (!TYPE_ACCESS.equals(claims.get(CLAIM_TYPE, String.class))) {
            throw new JwtException("Invalid token type");
        }
        return claims;
    }

//    public String extractRole(String token) {
//        return parseAllClaims(token)
//                .get(CLAIM_ROLE, String.class);
//    }

    // Helpers
    private Claims parseAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
