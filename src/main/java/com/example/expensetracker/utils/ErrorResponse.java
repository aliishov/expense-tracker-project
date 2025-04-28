package com.example.expensetracker.utils;

import java.util.Map;

public record ErrorResponse(
        Map<String, String> errors
) { }
