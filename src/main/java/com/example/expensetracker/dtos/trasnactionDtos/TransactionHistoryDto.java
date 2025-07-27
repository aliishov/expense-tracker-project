package com.example.expensetracker.dtos.trasnactionDtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionHistoryDto {
    String getTitle();
    BigDecimal getAmount();
    LocalDateTime getOperationDate();
    String getType();
    String getCurrency();
}
