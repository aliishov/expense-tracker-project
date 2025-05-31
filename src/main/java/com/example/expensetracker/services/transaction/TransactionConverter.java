package com.example.expensetracker.services.transaction;

import com.example.expensetracker.dtos.trasnactionDtos.TransactionRequestDto;
import com.example.expensetracker.dtos.trasnactionDtos.TransactionResponseDto;
import com.example.expensetracker.models.transaction.Category;
import com.example.expensetracker.models.transaction.Currency;
import com.example.expensetracker.models.transaction.Transaction;
import com.example.expensetracker.models.transaction.Type;
import com.example.expensetracker.repositories.CategoryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionConverter {

    private final CategoryRepository categoryRepository;

    public Transaction convertToDomainTransaction(@Valid TransactionRequestDto transactionRequestDto, UUID userId) {
        Category category = (categoryRepository.findByName(transactionRequestDto.category()).isPresent())
                ? categoryRepository.findByName(transactionRequestDto.category()).get()
                : null;

        return Transaction.builder()
                .title(transactionRequestDto.title())
                .description(transactionRequestDto.description())
                .amount(transactionRequestDto.amount())
                .operationDate(transactionRequestDto.operationDate())
                .category(category)
                .type(Type.valueOf(transactionRequestDto.type()))
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(null)
                .currency(Currency.valueOf(transactionRequestDto.currency()))
                .recurring(transactionRequestDto.recurring())
                .isArchived(false)
                .build();
    }

    public TransactionResponseDto convertToTransactionResponse(Transaction newTransaction) {
        return new TransactionResponseDto(
                newTransaction.getTitle(),
                newTransaction.getDescription(),
                newTransaction.getAmount(),
                newTransaction.getOperationDate(),
                newTransaction.getCategory().getName(),
                newTransaction.getType().name(),
                newTransaction.getCurrency().name(),
                newTransaction.getRecurring()
        );
    }
}
