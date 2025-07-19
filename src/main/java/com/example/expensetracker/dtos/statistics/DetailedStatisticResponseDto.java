package com.example.expensetracker.dtos.statistics;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DetailedStatisticResponseDto(
        List<DailySummaryDto> dailySummaries,
        Map<String, BigDecimal> incomeByCategory,
        Map<String, BigDecimal> expenseByCategory,
        Map<String, Integer> categoryUsageCount,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        int transactionCount
) {
}
