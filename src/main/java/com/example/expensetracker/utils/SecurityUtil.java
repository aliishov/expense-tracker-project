package com.example.expensetracker.utils;

import com.example.expensetracker.models.enums.Category;
import com.example.expensetracker.models.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

public class SecurityUtil {

    private final static Marker MY_LOG_MARKER = MarkerFactory.getMarker("MY_LOGGER");
    private final static Logger LOGGER = LoggerFactory.getLogger("MY_LOGGER");

    public static void checkTransactionOwnership(Transaction transaction, UUID userId) {
        if (!transaction.getUserId().equals(userId)) {
            LOGGER.info(MY_LOG_MARKER, "User with ID: {} is not the owner of Transaction with ID: {}", userId, transaction.getId());
            throw new AccessDeniedException("Access Denied");
        }
    }

    public static void checkCategoryOwnership(Category category, UUID userId) {
        if (!category.getUserId().equals(userId)) {
            LOGGER.info(MY_LOG_MARKER, "User with ID: {} is not the owner of Category with ID: {}", userId, category.getId());
            throw new AccessDeniedException("Access Denied");
        }
    }
}
