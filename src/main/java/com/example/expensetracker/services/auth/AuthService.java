package com.example.expensetracker.services.auth;

import com.example.expensetracker.dtos.authDtos.LoginRequestDto;
import com.example.expensetracker.dtos.authDtos.LoginResponseDto;
import com.example.expensetracker.dtos.authDtos.MessageResponseDto;
import com.example.expensetracker.dtos.authDtos.RegisterRequestDto;
import com.example.expensetracker.models.Role;
import com.example.expensetracker.models.User;
import com.example.expensetracker.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

        LOGGER.info(MY_LOG_MARKER, "User with Email: {} registered successfully", requestDto.getEmail());
        return ResponseEntity.ok(new MessageResponseDto("User registered successfully"));
    }

    public ResponseEntity<LoginResponseDto> login(@Valid LoginRequestDto requestDto) {
    }
}
