package com.example.ecommerce.user.service;

import com.example.ecommerce.common.exception.BusinessException;
import com.example.ecommerce.common.exception.ErrorCode;
import com.example.ecommerce.security.jwt.JwtService;
import com.example.ecommerce.security.session.SessionService;
import com.example.ecommerce.user.dto.LoginRequest;
import com.example.ecommerce.user.dto.RegisterRequest;
import com.example.ecommerce.user.entity.CustomUserDetails;
import com.example.ecommerce.user.entity.User;
import com.example.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SessionService sessionService;
    private final CustomUserDetailsService userDetailsService;

    // ----------------------------------------------------------------
    // Register
    // ----------------------------------------------------------------

    @Transactional
    public AuthResult register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhone(request.phone());

        userRepository.save(user);

        log.info("User registered: {}", user.getEmail());
        return buildAuthResult(user.getId(), user.getEmail());
    }

    // ----------------------------------------------------------------
    // Login
    // ----------------------------------------------------------------

    @Transactional(readOnly = true)
    public AuthResult login(LoginRequest request) {
        // Hatalı credentials → AuthenticationManager BadCredentialsException fırlatır
        // GlobalExceptionHandler yakalar → 401 döner
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        log.info("User logged in: {}", principal.getEmail());
        return buildAuthResult(principal.getId(), principal.getEmail());
    }

    // ----------------------------------------------------------------
    // Refresh
    // ----------------------------------------------------------------

    public AuthResult refresh(String compositeRefreshToken) {

        SessionService.RotationResult result = sessionService.rotateSession(compositeRefreshToken);

        if (result == null) {
            throw new BusinessException(ErrorCode.SESSION_EXPIRED);
        }

        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserById(result.userId());

        String newAccessToken = jwtService.generateToken(userDetails);

        return new AuthResult(newAccessToken, result.compositeRefreshToken());
    }

    // ----------------------------------------------------------------
    // Logout
    // ----------------------------------------------------------------

    public void logout(String compositeRefreshToken) {
        if (compositeRefreshToken == null) return;

        String sessionId = compositeRefreshToken.split("\\.")[0];
        sessionService.deleteSession(sessionId);
        log.info("Session deleted: {}", sessionId);
    }

    // ----------------------------------------------------------------
    // Helper
    // ----------------------------------------------------------------

    private AuthResult buildAuthResult(Long userId, String email) {
        String compositeToken = sessionService.createSession(userId);

        CustomUserDetails userDetails =
                (CustomUserDetails) userDetailsService.loadUserByUsername(email);

        String accessToken = jwtService.generateToken(userDetails);

        return new AuthResult(accessToken, compositeToken);
    }

    // ----------------------------------------------------------------
    // Result record — HTTP'den bağımsız, test edilebilir
    // ----------------------------------------------------------------

    public record AuthResult(String accessToken, String compositeRefreshToken) {
    }
}