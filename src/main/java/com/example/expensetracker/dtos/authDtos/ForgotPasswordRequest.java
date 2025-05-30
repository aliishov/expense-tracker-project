package com.example.expensetracker.dtos.authDtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record ForgotPasswordRequest(
        @NotEmpty(message = "Email should not be empty")
        @NotBlank(message = "Email name should not be empty")
        @Email
        String email
) {
}
