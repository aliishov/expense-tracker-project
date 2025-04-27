package com.example.expensetracker.controllers;

import com.example.expensetracker.dtos.authDtos.MessageResponseDto;
import com.example.expensetracker.dtos.authDtos.RegisterRequestDto;
import com.example.expensetracker.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Authentication & Authorization Controller",
        description = "Manage Authentication & Authorization in Project")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponseDto> register(@Valid @RequestBody RegisterRequestDto requestDto) {
        return userService.register(requestDto);
    }
}
