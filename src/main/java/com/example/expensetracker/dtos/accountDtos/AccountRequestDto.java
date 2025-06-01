package com.example.expensetracker.dtos.accountDtos;

import com.example.expensetracker.models.enums.Currency;

import java.util.UUID;

public record AccountRequestDto(
        Currency currency,
        UUID userId,
        String name
) {
}
