package com.example.expensetracker.dtos.categoyDto;

import java.util.UUID;

public record CategoryRequestDto(
        String name,
        String type,
        UUID userId
) {
}
