package com.example.expensetracker.services.transaction;

import com.example.expensetracker.dtos.trasnactionDtos.TransactionRequestDto;
import com.example.expensetracker.dtos.trasnactionDtos.TransactionResponseDto;
import com.example.expensetracker.models.transaction.Transaction;
import com.example.expensetracker.repositories.TransactionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionConverter transactionConverter;

    private final static Marker MY_LOG_MARKER = MarkerFactory.getMarker("MY_LOGGER");
    private final static Logger LOGGER = LoggerFactory.getLogger("MY_LOGGER");

    public ResponseEntity<TransactionResponseDto> create(@Valid TransactionRequestDto transactionRequestDto) {
        LOGGER.info(MY_LOG_MARKER, "Creating new transaction");

    }
}
