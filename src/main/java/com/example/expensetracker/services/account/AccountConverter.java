package com.example.expensetracker.services.account;

import com.example.expensetracker.dtos.accountDtos.AccountRequestDto;
import com.example.expensetracker.dtos.accountDtos.AccountResponseDto;
import com.example.expensetracker.models.balance.Account;
import com.example.expensetracker.models.enums.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountConverter {
    public Account convertToDomainAccount(AccountRequestDto accountRequestDto) {
        return Account.builder()
                .balance(BigDecimal.valueOf(0.00))
                .currency(Currency.USD)
                .userId(accountRequestDto.userId())
                .name(accountRequestDto.name())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(null)
                .build();
    }

    public AccountResponseDto convertToAccountResponse(Account account) {
        return new AccountResponseDto(
                account.getBalance(),
                account.getCurrency().name(),
                account.getName()
        );
    }
}
