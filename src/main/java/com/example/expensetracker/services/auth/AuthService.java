package com.example.expensetracker.services.auth;

import com.example.expensetracker.dtos.authDtos.*;
import com.example.expensetracker.models.mail.EmailNotificationSubject;
import com.example.expensetracker.models.user.Role;
import com.example.expensetracker.models.user.TokenType;
import com.example.expensetracker.models.user.User;
import com.example.expensetracker.repositories.UserRepository;
import com.example.expensetracker.services.mail.EmailSenderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailSenderService emailSenderService;
    private final TokenService tokenService;

    private final static Marker MY_LOG_MARKER = MarkerFactory.getMarker("MY_LOGGER");
    private final static Logger LOGGER = LoggerFactory.getLogger("MY_LOGGER");

    public ResponseEntity<MessageResponseDto> register(@Valid RegisterRequestDto requestDto) {
        LOGGER.info(MY_LOG_MARKER, "Registering a new user with email: {}", requestDto.getEmail());

        var newUser = User.builder()
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .role(Role.ROLE_USER)
                .imageUrl(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(null)
                .isAuthenticated(false)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isCredentialsNonExpired(true)
                .isAccountNonLocked(true)
                .build();

        userRepository.save(newUser);

        String token = tokenService.generateToken(newUser.getId(), TokenType.EMAIL_CONFIRMATION_TOKEN);
        String confirmationLink = "http://localhost:8080/api/v1/auth/email/confirm?token=" + token;

        Map<String, String> placeholders = Map.of(
                "confirmation_link", confirmationLink
        );

        emailSenderService.sendEmail(newUser.getEmail(), EmailNotificationSubject.EMAIL_CONFIRMATION_NOTIFICATION, placeholders);

        LOGGER.info(MY_LOG_MARKER, "User with Email: {} registered successfully", requestDto.getEmail());
        return ResponseEntity.ok(new MessageResponseDto("User registered successfully"));
    }

    public ResponseEntity<LoginResponseDto> login(@Valid LoginRequestDto requestDto) {
        LOGGER.info(MY_LOG_MARKER, "Login requested: {}", requestDto.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    requestDto.getEmail(),
                    requestDto.getPassword()
                )
        );

        var user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        LOGGER.info(MY_LOG_MARKER, "User with Email: {} successfully login", user.getEmail());

        return ResponseEntity.ok(new LoginResponseDto(accessToken, refreshToken));
    }

    @Transactional
    public ResponseEntity<MessageResponseDto> confirmEmail(@NotNull String token) {
        LOGGER.info(MY_LOG_MARKER, "Confirming email with token: {}", token);

        UUID userId;
        try {
            userId = tokenService.validateToken(token);
        } catch (IllegalArgumentException e) {
            LOGGER.error(MY_LOG_MARKER, "Invalid token provided", e);
            return ResponseEntity.badRequest().body(new MessageResponseDto("Invalid token provided"));
        }

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setIsAuthenticated(true);
        userRepository.save(user);

        LOGGER.info(MY_LOG_MARKER, "Email confirmed successfully for user with email: {}", user.getEmail());
        return ResponseEntity.ok(new MessageResponseDto("Email confirmed successfully"));
    }

    public ResponseEntity<MessageResponseDto> forgotPassword(@Valid ForgotPasswordRequest requestDto) {
        LOGGER.info(MY_LOG_MARKER, "Processing forgot password for Email: {}", requestDto.email());

        var user = userRepository.findByEmail(requestDto.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = tokenService.generateToken(user.getId(), TokenType.FORGOT_PASSWORD_TOKEN);
        String resetLink = "http://localhost:8080/api/v1/auth/password/reset?token=" + token;

        Map<String, String> placeholders = Map.of(
                "reset_link", resetLink
        );

        emailSenderService.sendEmail(user.getEmail(), EmailNotificationSubject.FORGOT_PASSWORD, placeholders);

        LOGGER.info(MY_LOG_MARKER, "Forgot password email sent to: {}", user.getEmail());

        return ResponseEntity.ok(new MessageResponseDto("Forgot password email sent successfully"));
    }

    public ResponseEntity<MessageResponseDto> resetPassword(@Valid ResetPasswordRequest request) {
        LOGGER.info(MY_LOG_MARKER, "Resetting password");

        if (!request.newPassword().equals(request.passwordRepeated())) {
            LOGGER.error(MY_LOG_MARKER, "New password and repeated password do not match");
            return ResponseEntity.badRequest().body(new MessageResponseDto("New password and repeated password do not match"));
        }

        UUID userId;
        try {
            userId = tokenService.validateToken(request.token());
        } catch (IllegalArgumentException e) {
            LOGGER.error(MY_LOG_MARKER, "Invalid token provided", e);
            return ResponseEntity.badRequest().body(new MessageResponseDto("Invalid token provided"));
        }

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        return ResponseEntity.ok(new MessageResponseDto("Password reset successfully"));
    }

    public ResponseEntity<MessageResponseDto> resendConfirmation(@Valid ResendConfirmationRequest request) {
        LOGGER.info(MY_LOG_MARKER, "Resending confirmation email");

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = tokenService.generateToken(user.getId(), TokenType.EMAIL_CONFIRMATION_TOKEN);
        String confirmationLink = "http://localhost:8080/api/v1/auth/email/confirm?token=" + token;

        Map<String, String> placeholders = Map.of(
                "confirmation_link", confirmationLink
        );

        emailSenderService.sendEmail(user.getEmail(), EmailNotificationSubject.EMAIL_CONFIRMATION_NOTIFICATION, placeholders);

        LOGGER.info(MY_LOG_MARKER, "Confirmation email sent to: {}", user.getEmail());
        return ResponseEntity.ok(new MessageResponseDto("Confirmation email sent successfully"));
    }

    public ResponseEntity<LoginResponseDto> refreshToken(@Valid RefreshTokenRequest request) {
        LOGGER.info(MY_LOG_MARKER, "Refreshing token");

        String email = jwtService.extractUsername(request.refreshToken());
        if (email == null) {
            LOGGER.error(MY_LOG_MARKER, "Invalid refresh token provided");
            return ResponseEntity.badRequest().body(new LoginResponseDto(null, null));
        }

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        LOGGER.info(MY_LOG_MARKER, "Token refreshed successfully for user with Email: {}", user.getEmail());
        return ResponseEntity.ok(new LoginResponseDto(newAccessToken, newRefreshToken));
    }
}
