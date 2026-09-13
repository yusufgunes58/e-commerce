package com.example.ecommerce.security.jwt;

import com.example.ecommerce.security.JwtCookieUtil;
import com.example.ecommerce.user.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Her istekte bir kez çalışır.
 * Access token'ı HttpOnly cookie'den okur.

 * Cookie → token al → JWT doğrula → userId çıkar
 * → DB'den UserDetails yükle → SecurityContext

 * Neden DB'den yüklüyoruz?
 * active=false olan veya silinen kullanıcı,
 * access token süresi dolmadan engellensin.

 * Yüksek trafikte bu yapı Redis cache'e taşınabilir.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final JwtCookieUtil cookieUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        cookieUtil.extractAccessToken(request)
                .ifPresent(this::authenticate);

        filterChain.doFilter(request, response);
    }

    private void authenticate(String token) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }

        try {
            // JWT bir kez parse edilir.
            Claims claims = jwtService.parseAccessToken(token);
            // JWT subject = userId
            Long userId = Long.valueOf(claims.getSubject());
            // Kullanıcı DB'den yüklenir.
            UserDetails userDetails =
                    userDetailsService.loadUserById(userId);
            // active=false ise authentication oluşturulmaz.
            if (!userDetails.isEnabled()) {
                log.debug("User is disabled: {}", userId);
                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            log.debug("Authenticated user: {}", userId);

        } catch (ExpiredJwtException e) {
            log.debug("Access token expired");

        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid access token");
        }
    }
}
