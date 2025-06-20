package com.example.expensetracker.services.account;

import com.example.expensetracker.dtos.accountDtos.*;
import com.example.expensetracker.models.balance.Account;
import com.example.expensetracker.models.enums.Currency;
import com.example.expensetracker.repositories.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountConverter accountConverter;

    private final static Marker MY_LOG_MARKER = MarkerFactory.getMarker("MY_LOGGER");
    private final static Logger LOGGER = LoggerFactory.getLogger("MY_LOGGER");

    public void createAccount(AccountRequestDto accountRequestDto) {
        LOGGER.info(MY_LOG_MARKER, "Creating new account with Name: {} for User with ID: {}", accountRequestDto.name(), accountRequestDto.userId());

        Account newAccount = accountConverter.convertToDomainAccount(accountRequestDto);
        accountRepository.save(newAccount);

        LOGGER.info(MY_LOG_MARKER, "Account with Name: {} successfully created", newAccount.getName());
    }

    public ResponseEntity<AccountResponseDto> charge(AccountChargeDto accountChargeDto, UUID userId) {
        LOGGER.info(MY_LOG_MARKER, "Charge User Account with user ID: {} and account Balance: {}", userId, accountChargeDto.amount());

        Account account = getAccountByUserId(userId);

        BigDecimal convertedAmount = convert(accountChargeDto.amount(),
                Currency.valueOf(accountChargeDto.currency()), account.getCurrency());

        BigDecimal newBalance = account.getBalance().add(convertedAmount);
        account.setBalance(newBalance);
        accountRepository.save(account);

        AccountResponseDto accountResponseDto = accountConverter.convertToAccountResponse(account);
        LOGGER.info(MY_LOG_MARKER, "Account with user ID: {} successfully charged with amount: {}", userId, accountChargeDto.amount());
        return ResponseEntity.ok(accountResponseDto);
    }

    public ResponseEntity<AccountResponseDto> convert(CurrencyConvertDto currencyConvertDto, UUID userId) {
        LOGGER.info(MY_LOG_MARKER, "Converting User Account Currency with user ID: {} to {}", userId, currencyConvertDto.newCurrency());

        Account account = getAccountByUserId(userId);

        BigDecimal convertedAmount = convert(account.getBalance(),
                account.getCurrency(), Currency.valueOf(currencyConvertDto.newCurrency()));

        account.setBalance(convertedAmount);
        account.setCurrency(Currency.valueOf(currencyConvertDto.newCurrency()));
        accountRepository.save(account);

        AccountResponseDto accountResponseDto = accountConverter.convertToAccountResponse(account);
        LOGGER.info(MY_LOG_MARKER, "Account Currency with user ID: {} successfully converted", userId);
        return ResponseEntity.ok(accountResponseDto);
    }

    public ResponseEntity<AccountResponseDto> update(AccountUpdateDto accountUpdateDto, UUID userId) {
        LOGGER.info(MY_LOG_MARKER, "Updating User Account with user ID: {}", userId);

        Account account = getAccountByUserId(userId);

        account.setBalance(accountUpdateDto.newBalance());
        account.setCurrency(Currency.valueOf(accountUpdateDto.newCurrency()));
        account.setName(accountUpdateDto.newName());
        accountRepository.save(account);

        LOGGER.info(MY_LOG_MARKER, "User Account with ID: {} successfully updated", userId);
        return ResponseEntity.ok(accountConverter.convertToAccountResponse(account));
    }

    private Account getAccountByUserId(UUID userId) {
        return accountRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    LOGGER.info(MY_LOG_MARKER, "Account with user ID: {} not found", userId);
                    return new EntityNotFoundException("Account not found");
                });
    }

    private BigDecimal convert(BigDecimal amount, Currency fromCurrency, Currency toCurrency) {
        if (fromCurrency == toCurrency) {
            return amount;
        }

        BigDecimal fromRate = BigDecimal.valueOf(fromCurrency.getVal());
        BigDecimal toRate = BigDecimal.valueOf(toCurrency.getVal());

        return amount
                .multiply(fromRate)
                .divide(toRate, 2, RoundingMode.HALF_UP);
    }
}
