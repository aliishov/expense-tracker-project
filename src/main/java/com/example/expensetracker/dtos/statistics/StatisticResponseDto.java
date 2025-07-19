package com.example.expensetracker.dtos.statistics;

import java.math.BigDecimal;

public record StatisticResponseDto(
        BigDecimal income,
        BigDecimal expense,
        String mostIncomeCategory,
        String lessIncomeCategory,
        String mostExpenseCategory,
        String lessExpenseCategory,
        String mostUsedCategory,
        String lessUsedCategory,
        Integer transactionCount
) {
}

