package com.example.expensetracker.dtos.trasnactionDtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionUpdateDto(
        String title,
        String description,
        BigDecimal amount,
        LocalDateTime operationDate,
        String category,
        String type,
        String currency,
        Boolean recurring
) {
}
