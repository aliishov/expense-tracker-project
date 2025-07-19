package com.example.expensetracker.services.transaction;

import com.example.expensetracker.dtos.trasnactionDtos.TransactionRequestDto;
import com.example.expensetracker.dtos.trasnactionDtos.TransactionResponseDto;
import com.example.expensetracker.models.balance.Account;
import com.example.expensetracker.models.transaction.Category;
import com.example.expensetracker.models.enums.Currency;
import com.example.expensetracker.models.transaction.RecurringType;
import com.example.expensetracker.models.transaction.Transaction;
import com.example.expensetracker.models.enums.Type;
import com.example.expensetracker.repositories.AccountRepository;
import com.example.expensetracker.repositories.CategoryRepository;
import com.example.expensetracker.repositories.TransactionRepository;
import com.example.expensetracker.utils.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionConverter transactionConverter;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    private final static Marker MY_LOG_MARKER = MarkerFactory.getMarker("MY_LOGGER");
    private final static Logger LOGGER = LoggerFactory.getLogger("MY_LOGGER");

    public ResponseEntity<List<TransactionResponseDto>> getAll(UUID userId) {
        LOGGER.info(MY_LOG_MARKER, "Getting all transactions for user with ID: {}", userId);
        List<Transaction> transactions = transactionRepository.findAllByUserId(userId);

        if (transactions.isEmpty()) {
            LOGGER.info(MY_LOG_MARKER, "No transactions found for user with ID: {}", userId);
            return ResponseEntity.noContent().build();
        }

        List<TransactionResponseDto> transactionResponses = transactions.stream()
                .map(transactionConverter::convertToTransactionResponse)
                .toList();

        LOGGER.info(MY_LOG_MARKER, "Transactions for user with ID: {} retrieved successfully", userId);
        return ResponseEntity.ok(transactionResponses);
    }

    public ResponseEntity<TransactionResponseDto> getById(UUID userId, Long transactionId) {
        LOGGER.info(MY_LOG_MARKER, "Getting transaction with ID: {} for user with ID: {}", transactionId, userId);
        Transaction transaction = getTransactionById(transactionId);

        SecurityUtil.checkTransactionOwnership(transaction, userId);

        TransactionResponseDto transactionResponseDto = transactionConverter.convertToTransactionResponse(transaction);

        LOGGER.info(MY_LOG_MARKER, "Transaction with ID: {} retrieved successfully for user with ID: {}", transactionId, userId);
        return ResponseEntity.ok(transactionResponseDto);
    }

    public ResponseEntity<TransactionResponseDto> create(@Valid TransactionRequestDto transactionRequestDto, UUID userId) throws BadRequestException {
        LOGGER.info(MY_LOG_MARKER, "Creating new transaction");

        validateRecurringDto(transactionRequestDto);

        Account account = getAccountByUserId(userId);
        Transaction newTransaction = transactionConverter.convertToDomainTransaction(transactionRequestDto, userId);

        if (newTransaction.getType() == Type.INCOME) {
            account.setBalance(account.getBalance().add(newTransaction.getAmount()));
        } else {
            if (account.getBalance().compareTo(transactionRequestDto.amount()) < 0) {
                LOGGER.error(MY_LOG_MARKER, "Insufficient balance for user {}. Required: {}, Available: {}",
                        userId, transactionRequestDto.amount(), account.getBalance());
                throw new BadRequestException("Balance not enough");
            }
            account.setBalance(account.getBalance().subtract(newTransaction.getAmount()));
        }

        transactionRepository.save(newTransaction);
        accountRepository.save(account);

        TransactionResponseDto transactionResponseDto = transactionConverter.convertToTransactionResponse(newTransaction);

        LOGGER.info(MY_LOG_MARKER, "Transaction with Title: {} created successfully", transactionResponseDto.title());
        return ResponseEntity.ok(transactionResponseDto);
    }

    public ResponseEntity<TransactionResponseDto> update(Long transactionId,
                                                         @Valid TransactionRequestDto transactionRequestDto,
                                                         UUID userId) throws BadRequestException {
        LOGGER.info(MY_LOG_MARKER, "Updating transaction with ID: {}", transactionId);

        Transaction transaction = getTransactionById(transactionId);

        SecurityUtil.checkTransactionOwnership(transaction, userId);

        updateTransaction(transaction, transactionRequestDto);
        transactionRepository.save(transaction);

        TransactionResponseDto updatedTransaction = transactionConverter.convertToTransactionResponse(transaction);

        LOGGER.info(MY_LOG_MARKER, "Transaction with ID: {} updated successfully", transactionId);
        return ResponseEntity.ok(updatedTransaction);
    }

    public ResponseEntity<Void> delete(Long transactionId, UUID userId) {
        LOGGER.info(MY_LOG_MARKER, "Deleting transaction with ID: {}", transactionId);

        Transaction transaction = getTransactionById(transactionId);

        SecurityUtil.checkTransactionOwnership(transaction, userId);
        transactionRepository.delete(transaction);

        LOGGER.info(MY_LOG_MARKER, "Transaction with ID: {} deleted successfully", transactionId);
        return ResponseEntity.noContent().build();
    }

    private Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> {
                    LOGGER.info(MY_LOG_MARKER, "Transaction with ID: {} not found", transactionId);
                    return new EntityNotFoundException("Transaction not found");
                });
    }

    private void updateTransaction(Transaction transaction, TransactionRequestDto dto) throws BadRequestException {
        if (dto.title() != null) transaction.setTitle(dto.title());
        if (dto.description() != null) transaction.setDescription(dto.description());
        if (dto.operationDate() != null) transaction.setOperationDate(dto.operationDate());

        if (dto.amount() != null && !dto.amount().equals(transaction.getAmount())) {
            updateAccountBalanceOnAmountChange(transaction, dto.amount());
            transaction.setAmount(dto.amount());
        }

        if (dto.category() != null) {
            Category category = categoryRepository.findByName(dto.category())
                    .orElseThrow(() -> new BadRequestException("Category not found: " + dto.category()));
            transaction.setCategory(category);
        }

        if (dto.type() != null) transaction.setType(Type.valueOf(dto.type()));
        if (dto.currency() != null) transaction.setCurrency(Currency.valueOf(dto.currency()));
        if (dto.recurring() != null) transaction.setRecurring(dto.recurring());

        if (Boolean.TRUE.equals(dto.recurring())) {
            if (dto.recurringType() == null) {
                throw new BadRequestException("Recurring type is required for recurring transactions");
            }
            transaction.setRecurringType(RecurringType.valueOf(dto.recurringType()));
        }
    }

    private Account getAccountByUserId(UUID userId) {
        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    LOGGER.info(MY_LOG_MARKER, "Account with user ID: {} not found", userId);
                    return new EntityNotFoundException("Account not found");
                });
    }

    private void validateRecurringDto(TransactionRequestDto transactionRequestDto) throws BadRequestException {
        if (Boolean.TRUE.equals(transactionRequestDto.recurring()) && transactionRequestDto.recurringType() == null) {
            LOGGER.error(MY_LOG_MARKER, "Invalid recurring transaction request");
            throw new BadRequestException("Recurring type is required for recurring transactions");
        }
    }

    private void updateAccountBalanceOnAmountChange(Transaction transaction, BigDecimal newAmount) {
        Account account = getAccountByUserId(transaction.getUserId());
        BigDecimal currentAmount = transaction.getAmount();
        BigDecimal updatedBalance = account.getBalance().add(currentAmount).subtract(newAmount);
        account.setBalance(updatedBalance);
        accountRepository.save(account);
    }
}
