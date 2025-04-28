package com.example.expensetracker.dtos.authDtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequestDto {
    @NotEmpty(message = "Email should not be empty")
    @NotBlank(message = "Email name should not be empty")
    @Email
    private String email;

    @NotEmpty(message = "Password name should not be empty")
    @NotBlank(message = "Password name should not be empty")
    @Size(min = 8, message = "Password should be greater than 8 characters")
    private String password;
}
