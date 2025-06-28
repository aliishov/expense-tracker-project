package com.example.expensetracker.controllers;

import com.example.expensetracker.dtos.accountDtos.AccountChargeDto;
import com.example.expensetracker.dtos.accountDtos.AccountResponseDto;
import com.example.expensetracker.dtos.accountDtos.AccountUpdateDto;
import com.example.expensetracker.dtos.accountDtos.CurrencyConvertDto;
import com.example.expensetracker.models.user.User;
import com.example.expensetracker.services.account.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Validated
@Tag(name = "Accounts Controller",
        description = "Manage Balances in Project")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/me")
    public ResponseEntity<AccountResponseDto> getByUserId(@AuthenticationPrincipal User user) {
        UUID userId = user.getId();
        return accountService.getByUserId(userId);
    }

    @PostMapping("/me/charge")
    public ResponseEntity<AccountResponseDto> chargeAccount(@RequestBody AccountChargeDto accountChargeDto,
                                                            @AuthenticationPrincipal User user) {
        UUID userId = user.getId();
        return accountService.charge(accountChargeDto, userId);
    }

    @PatchMapping("/me/convert-currency")
    public ResponseEntity<AccountResponseDto> chargeAccount(@RequestBody CurrencyConvertDto currencyConvertDto,
                                                            @AuthenticationPrincipal User user) {
        UUID userId = user.getId();
        return accountService.convert(currencyConvertDto, userId);
    }

    @PatchMapping("/me/update")
    public ResponseEntity<AccountResponseDto> updateAccount(@RequestBody AccountUpdateDto accountUpdateDto,
                                                            @AuthenticationPrincipal User user) {
        UUID userId = user.getId();
        return accountService.update(accountUpdateDto, userId);
    }
}
