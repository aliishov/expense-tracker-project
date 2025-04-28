package com.example.expensetracker.dtos.authDtos;

public record LoginResponseDto(
        String accessToken,
        String refreshToken
) {

}
