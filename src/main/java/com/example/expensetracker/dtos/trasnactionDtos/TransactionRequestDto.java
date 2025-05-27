package com.example.expensetracker.dtos.trasnactionDtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionRequestDto(
        String title,
        String description,
        BigDecimal amount,
        LocalDateTime operationDate,
        String category,
        String type,
        UUID userId,
        String currency,
        Boolean recurring
) {
}
