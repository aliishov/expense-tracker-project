package com.example.expensetracker.utils.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TokenNotFoundException extends RuntimeException {
    private final String msg;
}
