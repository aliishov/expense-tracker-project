package com.example.expensetracker.controllers;

import com.example.expensetracker.dtos.trasnactionDtos.TransactionHistoryDto;
import com.example.expensetracker.models.user.User;
import com.example.expensetracker.services.history.HistoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
@Validated
@Tag(name = "Transaction History Controller",
        description = "Manage Transactions History in Project")
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping()
    public ResponseEntity<List<TransactionHistoryDto>> getTransactionHistory(@RequestParam(required = false, defaultValue = "2000-01-01") LocalDate startDate,
                                                                             @RequestParam(required = false, defaultValue = "2099-12-31") LocalDate endDate,
                                                                             @RequestParam(required = false, defaultValue = "0") Integer page,
                                                                             @RequestParam(required = false, defaultValue = "15") Integer pageSize,
                                                                             @AuthenticationPrincipal User user) {
        UUID userId = user.getId();
        return historyService.getTransactionHistory(startDate, endDate, page, pageSize, userId);
    }
}
