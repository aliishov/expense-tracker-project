package com.example.expensetracker.services.statistics;

import com.example.expensetracker.dtos.statistics.DailySummaryDto;
import com.example.expensetracker.dtos.statistics.DetailedStatisticResponseDto;
import com.example.expensetracker.dtos.statistics.StatisticResponseDto;
import com.example.expensetracker.models.enums.Type;
import com.example.expensetracker.models.transaction.Transaction;
import com.example.expensetracker.repositories.TransactionRepository;
import com.example.expensetracker.utils.exceptions.NoContentException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final TransactionRepository transactionRepository;

    private final static Marker MY_LOG_MARKER = MarkerFactory.getMarker("MY_LOGGER");
    private final static Logger LOGGER = LoggerFactory.getLogger("MY_LOGGER");

    public ResponseEntity<StatisticResponseDto> getSummary(UUID userId) {
        LOGGER.info(MY_LOG_MARKER, "Received Request for generalized statistics for user {}", userId);

        List<Transaction> transactions = transactionRepository.findAllByUserId(userId);

        isTransactionsEmpty(transactions, userId);

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;
        Map<String, BigDecimal> incomeByCategory = new HashMap<>();
        Map<String, BigDecimal> expenseByCategory = new HashMap<>();
        Map<String, Integer> usageCountByCategory = new HashMap<>();

        for (Transaction transaction : transactions) {
            String category = transaction.getCategory().getName();
            BigDecimal amount = transaction.getAmount();

            if (transaction.getType() == Type.INCOME) {
                income = income.add(amount);
                incomeByCategory.merge(category, amount, BigDecimal::add);
            } else {
                expense = expense.add(amount);
                expenseByCategory.merge(category, amount, BigDecimal::add);
            }

            usageCountByCategory.merge(category, 1, Integer::sum);
        }

        StatisticResponseDto response = new StatisticResponseDto(
                income,
                expense,
                getCategoryWithMaxValue(incomeByCategory),
                getCategoryWithMinValue(incomeByCategory),
                getCategoryWithMaxValue(expenseByCategory),
                getCategoryWithMinValue(expenseByCategory),
                getCategoryWithMaxValue(usageCountByCategory),
                getCategoryWithMinValue(usageCountByCategory),
                transactions.size()
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<DetailedStatisticResponseDto> getDetailedSummary(LocalDateTime startDate,
                                                                           LocalDateTime endDate,
                                                                           UUID userId) {
        LOGGER.info(MY_LOG_MARKER, "Received Request for detailed statistics for user {}", userId);

        List<Transaction> transactions = transactionRepository.findAllByUserIdAndOperationDateBetween(userId, startDate, endDate);

        isTransactionsEmpty(transactions, userId);

        Map<LocalDate, List<Transaction>> dailyTransactions = new HashMap<>();
        Map<String, BigDecimal> incomeByCategory = new HashMap<>();
        Map<String, BigDecimal> expenseByCategory = new HashMap<>();
        Map<String, Integer> categoryUsageCount = new HashMap<>();
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            LocalDate date = transaction.getOperationDate().toLocalDate();
            String categoryName = transaction.getCategory().getName();
            BigDecimal amount = transaction.getAmount();

            dailyTransactions
                    .computeIfAbsent(date, k -> new ArrayList<>())
                    .add(transaction);

            switch (transaction.getType()) {
                case INCOME -> {
                    incomeByCategory.merge(categoryName, amount, BigDecimal::add);
                    totalIncome = totalIncome.add(amount);
                }
                case EXPENSE -> {
                    expenseByCategory.merge(categoryName, amount, BigDecimal::add);
                    totalExpense = totalExpense.add(amount);
                }
            }

            categoryUsageCount.merge(categoryName, 1, Integer::sum);
        }

        List<DailySummaryDto> dailySummaries = dailyTransactions.entrySet().stream()
                .map(this::getDailySummaryDto)
                .toList();

        DetailedStatisticResponseDto response = new DetailedStatisticResponseDto(
                dailySummaries,
                incomeByCategory,
                expenseByCategory,
                categoryUsageCount,
                totalIncome,
                totalExpense,
                transactions.size()
        );

        return ResponseEntity.ok(response);
    }

    private DailySummaryDto getDailySummaryDto(Map.Entry<LocalDate, List<Transaction>> entry) {
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;

        for (Transaction transaction : entry.getValue()) {
            BigDecimal amount = transaction.getAmount();
            if (transaction.getType() == Type.INCOME) income = income.add(amount);
            else expense = expense.add(amount);
        }

        return new DailySummaryDto(
                entry.getKey(),
                income,
                expense,
                entry.getValue().size()
        );
    }

    private String getCategoryWithMaxValue(Map<String, ? extends Number> map) {
        return map.entrySet().stream()
                .max(Comparator.comparing(e -> new BigDecimal(e.getValue().toString())))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private String getCategoryWithMinValue(Map<String, ? extends Number> map) {
        return map.entrySet().stream()
                .min(Comparator.comparing(e -> new BigDecimal(e.getValue().toString())))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private void isTransactionsEmpty(List<Transaction> transactions, UUID userId) {
        if (transactions.isEmpty()) {
            LOGGER.info(MY_LOG_MARKER, "No transactions found for user {}", userId);
            throw new NoContentException("No transactions found");
        }
    }
}
