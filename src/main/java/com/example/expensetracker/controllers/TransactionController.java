package com.example.expensetracker.controllers;

import com.example.expensetracker.services.transaction.TransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Validated
@Tag(name = "Transaction Controller",
        description = "Manage Transactions in Project")
public class TransactionController {

    private final TransactionService transactionService;
}
