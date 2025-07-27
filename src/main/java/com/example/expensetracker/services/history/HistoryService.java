package com.example.expensetracker.services.history;

import com.example.expensetracker.dtos.trasnactionDtos.TransactionHistoryDto;
import com.example.expensetracker.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final TransactionRepository transactionRepository;

    private final static Marker MY_LOG_MARKER = MarkerFactory.getMarker("MY_LOGGER");
    private final static Logger LOGGER = LoggerFactory.getLogger("MY_LOGGER");


    public ResponseEntity<List<TransactionHistoryDto>> getTransactionHistory(LocalDate startDate, LocalDate endDate,
                                                                             Integer page, Integer pageSize,
                                                                             UUID userId) {
        LOGGER.info(MY_LOG_MARKER, "Received request for transaction history for user {}",  userId);

        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        Pageable pageable = PageRequest.of(page, pageSize);
        List<TransactionHistoryDto> transactionHistoryDtos = transactionRepository.findAllByUserIdAndOperationDateBetween(userId,
                startTime, endTime, pageable).toList();

        if (transactionHistoryDtos.isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok(transactionHistoryDtos);
    }
}
