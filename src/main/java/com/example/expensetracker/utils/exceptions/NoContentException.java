package com.example.expensetracker.utils.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NoContentException extends RuntimeException {
    private final String message;
}
