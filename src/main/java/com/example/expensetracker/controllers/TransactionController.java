package com.example.expensetracker.controllers;

import com.example.expensetracker.dtos.trasnactionDtos.TransactionRequestDto;
import com.example.expensetracker.dtos.trasnactionDtos.TransactionResponseDto;
import com.example.expensetracker.dtos.trasnactionDtos.TransactionUpdateDto;
import com.example.expensetracker.services.transaction.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Validated
@Tag(name = "Transaction Controller",
        description = "Manage Transactions in Project")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<TransactionResponseDto>> getAll(@PathVariable UUID userId) {
        return transactionService.getAll(userId);
    }

    @GetMapping("/{userId}/{transactionId}")
    public ResponseEntity<TransactionResponseDto> getById(@PathVariable UUID userId,
                                                          @PathVariable Long transactionId) {
        return transactionService.getById(userId, transactionId);
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDto> create(@RequestBody @Valid TransactionRequestDto transactionRequestDto) {
        return transactionService.create(transactionRequestDto);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDto> update(@PathVariable Long transactionId,
                                                         @RequestBody @Valid TransactionUpdateDto transactionUpdateDto) {
        return transactionService.update(transactionId, transactionUpdateDto);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> delete(@PathVariable Long transactionId) {
        return transactionService.delete(transactionId);
    }
}
