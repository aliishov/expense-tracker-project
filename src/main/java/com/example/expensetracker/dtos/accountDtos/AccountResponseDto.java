package com.example.expensetracker.dtos.accountDtos;

import java.math.BigDecimal;

public record AccountResponseDto(
        BigDecimal balance,
        String currency,
        String name
) {
}
