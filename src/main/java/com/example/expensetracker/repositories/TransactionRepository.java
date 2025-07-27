package com.example.expensetracker.repositories;

import com.example.expensetracker.dtos.trasnactionDtos.TransactionHistoryDto;
import com.example.expensetracker.models.transaction.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT th.title AS title, th.amount AS amount, th.operationDate AS operationDate, th.type AS type, th.currency AS currency " +
            "FROM Transaction th " +
            "WHERE th.userId = :userId AND th.operationDate BETWEEN :startDate AND :endDate " +
            "ORDER BY th.operationDate DESC")
    Page<TransactionHistoryDto> findAllByUserIdAndOperationDateBetween(@Param("userId") UUID userId,
                                                                       @Param("startDate") LocalDateTime startDate,
                                                                       @Param("endDate") LocalDateTime endDate,
                                                                       Pageable pageable
    );
}
