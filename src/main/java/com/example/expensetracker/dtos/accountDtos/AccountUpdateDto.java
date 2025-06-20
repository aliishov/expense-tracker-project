package com.example.expensetracker.dtos.accountDtos;

import java.math.BigDecimal;

public record AccountUpdateDto(
        BigDecimal newBalance,
        String newCurrency,
        String newName
) {
}
