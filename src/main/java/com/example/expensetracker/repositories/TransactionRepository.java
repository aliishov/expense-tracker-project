package com.example.expensetracker.repositories;

import com.example.expensetracker.models.transaction.Transaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUserId(UUID userId);
    List<Transaction> findAllByRecurringTrueAndIsArchivedFalseAndNextExecutionBefore(LocalDateTime now);

    @EntityGraph(attributePaths = {"category"})
    List<Transaction> findAllByUserIdAndOperationDateBetween(UUID userId, LocalDateTime startDate, LocalDateTime endDate);
}
