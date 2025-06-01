package com.example.expensetracker.dtos.accountDtos;

import java.math.BigDecimal;

public record AccountChargeDto(
        BigDecimal amount,
        String currency
) {
}
