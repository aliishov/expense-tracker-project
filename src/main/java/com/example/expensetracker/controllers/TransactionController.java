package com.example.expensetracker.controllers;

import com.example.expensetracker.dtos.trasnactionDtos.TransactionRequestDto;
import com.example.expensetracker.dtos.trasnactionDtos.TransactionResponseDto;
import com.example.expensetracker.dtos.trasnactionDtos.TransactionUpdateDto;
import com.example.expensetracker.models.user.User;
import com.example.expensetracker.services.transaction.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<TransactionResponseDto>> getAll(@AuthenticationPrincipal User user) {
        UUID userId = user.getId();
        return transactionService.getAll(userId);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDto> getById(@PathVariable Long transactionId,
                                                          @AuthenticationPrincipal User user) {
        UUID userId = user.getId();
        return transactionService.getById(userId, transactionId);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping
    public ResponseEntity<TransactionResponseDto> create(@RequestBody @Valid TransactionRequestDto transactionRequestDto,
                                                         @AuthenticationPrincipal User user) throws BadRequestException {
        UUID userId = user.getId();
        return transactionService.create(transactionRequestDto, userId);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDto> update(@PathVariable Long transactionId,
                                                         @RequestBody @Valid TransactionUpdateDto transactionUpdateDto,
                                                         @AuthenticationPrincipal User user) {
        UUID userId = user.getId();
        return transactionService.update(transactionId, transactionUpdateDto, userId);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> delete(@PathVariable Long transactionId,
                                       @AuthenticationPrincipal User user) {
        UUID userId = user.getId();
        return transactionService.delete(transactionId, userId);
    }
}
