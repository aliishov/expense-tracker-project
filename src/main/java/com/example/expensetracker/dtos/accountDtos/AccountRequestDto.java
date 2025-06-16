package com.example.expensetracker.dtos.accountDtos;

import java.util.UUID;

public record AccountRequestDto(
        UUID userId,
        String name
) {
}
