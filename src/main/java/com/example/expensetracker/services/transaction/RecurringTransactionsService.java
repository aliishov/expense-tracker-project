package com.example.expensetracker.services.transaction;

import com.example.expensetracker.models.balance.Account;
import com.example.expensetracker.models.transaction.RecurringType;
import com.example.expensetracker.models.transaction.Transaction;
import com.example.expensetracker.repositories.AccountRepository;
import com.example.expensetracker.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@EnableAsync
public class RecurringTransactionsService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    private final static Marker MY_LOG_MARKER = MarkerFactory.getMarker("MY_LOGGER");
    private final static Logger LOGGER = LoggerFactory.getLogger("MY_LOGGER");

    @Async
    @Transactional
    @Scheduled(cron = "${task.recurring.transactions.cron}")
    public void processRecurringTransactions() {
        LOGGER.info(MY_LOG_MARKER, "Start processing recurring transactions");

        List<Transaction> transactions = transactionRepository
                .findAllByRecurringTrueAndIsArchivedFalseAndNextExecutionBefore(LocalDateTime.now());

        for (Transaction transaction : transactions) {
            UUID userId = transaction.getUserId();

            Optional<Account> optionalAccount = accountRepository.findByUserId(userId);
            if (optionalAccount.isEmpty()) {
                LOGGER.error(MY_LOG_MARKER, "Account not found for user {}", userId);
                continue;
            }
            Account account = optionalAccount.get();

            if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
                LOGGER.error(MY_LOG_MARKER, "Insufficient balance for user {}. Required: {}, Available: {}",
                        userId, transaction.getAmount(), account.getBalance());
                continue;
            }

            Transaction newTransaction = createCopyOf(transaction);
            account.setBalance(account.getBalance().subtract(newTransaction.getAmount()));

            transaction.setNextExecution(calculateNextExecution(LocalDateTime.now(), transaction.getRecurringType()));

            transactionRepository.saveAll(List.of(transaction, newTransaction));
            accountRepository.save(account);

            LOGGER.info(MY_LOG_MARKER, "Recurring transaction processed for user {}", userId);
        }
        LOGGER.info(MY_LOG_MARKER, "Finished processing recurring transactions");
    }

    private Transaction createCopyOf(Transaction original) {
        return Transaction.builder()
                .title(original.getTitle())
                .description(original.getDescription())
                .amount(original.getAmount())
                .operationDate(LocalDateTime.now())
                .category(original.getCategory())
                .type(original.getType())
                .userId(original.getUserId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(null)
                .currency(original.getCurrency())
                .recurring(false)
                .recurringType(null)
                .nextExecution(null)
                .isArchived(false)
                .build();
    }

    private LocalDateTime calculateNextExecution(LocalDateTime from, RecurringType type) {
        return switch (type) {
            case DAILY -> from.plusDays(1);
            case WEEKLY -> from.plusWeeks(1);
            case MONTHLY -> from.plusMonths(1);
            case YEARLY -> from.plusYears(1);
        };
    }
}
