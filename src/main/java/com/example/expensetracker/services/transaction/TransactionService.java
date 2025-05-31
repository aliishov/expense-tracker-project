package com.example.expensetracker.services.transaction;

import com.example.expensetracker.dtos.trasnactionDtos.TransactionRequestDto;
import com.example.expensetracker.dtos.trasnactionDtos.TransactionResponseDto;
import com.example.expensetracker.dtos.trasnactionDtos.TransactionUpdateDto;
import com.example.expensetracker.models.transaction.Category;
import com.example.expensetracker.models.transaction.Currency;
import com.example.expensetracker.models.transaction.Transaction;
import com.example.expensetracker.models.transaction.Type;
import com.example.expensetracker.repositories.CategoryRepository;
import com.example.expensetracker.repositories.TransactionRepository;
import com.example.expensetracker.utils.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionConverter transactionConverter;
    private final CategoryRepository categoryRepository;

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

    public ResponseEntity<TransactionResponseDto> create(@Valid TransactionRequestDto transactionRequestDto, UUID userId) {
        LOGGER.info(MY_LOG_MARKER, "Creating new transaction");

        Transaction newTransaction = transactionConverter.convertToDomainTransaction(transactionRequestDto, userId);
        transactionRepository.save(newTransaction);

        TransactionResponseDto transactionResponseDto = transactionConverter.convertToTransactionResponse(newTransaction);

        LOGGER.info(MY_LOG_MARKER, "Transaction with Title: {} created successfully", transactionResponseDto.title());
        return ResponseEntity.ok(transactionResponseDto);
    }

    public ResponseEntity<TransactionResponseDto> update(Long transactionId,
                                                         @Valid TransactionUpdateDto transactionUpdateDto,
                                                         UUID userId) {
        LOGGER.info(MY_LOG_MARKER, "Updating transaction with ID: {}", transactionId);

        Transaction transaction = getTransactionById(transactionId);

        SecurityUtil.checkTransactionOwnership(transaction, userId);

        updateTransaction(transaction, transactionUpdateDto);
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

    private void updateTransaction(Transaction transaction, TransactionUpdateDto transactionUpdateDto) {
        transaction.setTitle((transactionUpdateDto.title() != null)
                ? transactionUpdateDto.title() : transaction.getTitle());

        transaction.setDescription((transactionUpdateDto.description() != null)
                ? transactionUpdateDto.description() : transaction.getDescription());

        transaction.setAmount((transactionUpdateDto.amount() != null)
                ? transactionUpdateDto.amount() : transaction.getAmount());

        transaction.setOperationDate((transactionUpdateDto.operationDate() != null)
                ? transactionUpdateDto.operationDate() : transaction.getOperationDate());

        Category category = (categoryRepository.findByName(transactionUpdateDto.category()).isPresent())
                ? categoryRepository.findByName(transactionUpdateDto.category()).get()
                : null;
        transaction.setCategory(category);

        transaction.setType((transactionUpdateDto.type() != null)
                ? Type.valueOf(transactionUpdateDto.type()) : transaction.getType());

        transaction.setCurrency((transactionUpdateDto.currency() != null)
                ? Currency.valueOf(transactionUpdateDto.currency()) : transaction.getCurrency());

        transaction.setRecurring((transactionUpdateDto.recurring() != null)
                ? transactionUpdateDto.recurring() : transaction.getRecurring());
    }
}
