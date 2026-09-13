package com.example.ecommerce.user.controller;

import com.example.ecommerce.security.JwtCookieUtil;
import com.example.ecommerce.user.dto.LoginRequest;
import com.example.ecommerce.user.dto.RegisterRequest;
import com.example.ecommerce.user.entity.CustomUserDetails;
import com.example.ecommerce.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtCookieUtil cookieUtil;

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        AuthService.AuthResult result = authService.register(request);
        cookieUtil.addAuthCookies(response, result.accessToken(), result.compositeRefreshToken());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        AuthService.AuthResult result = authService.login(request);
        cookieUtil.addAuthCookies(response, result.accessToken(), result.compositeRefreshToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(
            HttpServletRequest  request,
            HttpServletResponse response
    ) {
        String compositeToken = cookieUtil.extractRefreshToken(request).orElse(null);
        AuthService.AuthResult result = authService.refresh(compositeToken);
        cookieUtil.addAuthCookies(response, result.accessToken(), result.compositeRefreshToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        String compositeToken = cookieUtil.extractRefreshToken(request).orElse(null);
        authService.logout(compositeToken);
        cookieUtil.clearAuthCookies(response);
        return ResponseEntity.noContent().build();
    }
}