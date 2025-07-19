package com.example.expensetracker.dtos.statistics;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailySummaryDto(
        LocalDate date,
        BigDecimal income,
        BigDecimal expense,
        int transactionCount
) {
}
