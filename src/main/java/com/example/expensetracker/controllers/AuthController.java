package com.example.expensetracker.controllers;

import com.example.expensetracker.dtos.authDtos.*;
import com.example.expensetracker.services.auth.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Authentication & Authorization Controller",
        description = "Manage Authentication & Authorization in Project")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponseDto> register(@Valid @RequestBody RegisterRequestDto requestDto) {
        return authService.register(requestDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        return authService.login(requestDto);
    }

    @GetMapping("/email/confirm")
    public ResponseEntity<MessageResponseDto> confirmEmail(@RequestParam String token) {
        return authService.confirmEmail(token);
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<MessageResponseDto> forgotPassword(@RequestBody @Valid ForgotPasswordRequest requestDto) {
        return authService.forgotPassword(requestDto);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<MessageResponseDto> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        return authService.resetPassword(request);
    }

    @PostMapping("/email/resend-confirmation")
    public ResponseEntity<MessageResponseDto> resendConfirmation(@RequestBody @Valid ResendConfirmationRequest request) {
        return authService.resendConfirmation(request);
    }
}
