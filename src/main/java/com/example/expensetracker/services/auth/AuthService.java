package com.example.expensetracker.services.auth;

import com.example.expensetracker.dtos.authDtos.LoginRequestDto;
import com.example.expensetracker.dtos.authDtos.LoginResponseDto;
import com.example.expensetracker.dtos.authDtos.MessageResponseDto;
import com.example.expensetracker.dtos.authDtos.RegisterRequestDto;
import com.example.expensetracker.models.mail.EmailNotificationSubject;
import com.example.expensetracker.models.user.Role;
import com.example.expensetracker.models.user.TokenType;
import com.example.expensetracker.models.user.User;
import com.example.expensetracker.repositories.UserRepository;
import com.example.expensetracker.services.mail.EmailSenderService;
import jakarta.validation.Valid;
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

import java.time.LocalDateTime;
import java.util.Map;

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
        String confirmationLink = "http://localhost:8010/api/v1/auth/email/confirm?token=" + token;

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
}
