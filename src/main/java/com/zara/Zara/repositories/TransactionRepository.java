package com.zara.Zara.repositories;

import com.zara.Zara.entities.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionNumber(String transactionNumber);

    @Query(value = "SELECT * FROM transactions where created_at between :startDate AND :endDate order by created_at DESC", nativeQuery = true)
    Page<Transaction> history(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query(value = "SELECT * FROM transactions where sender_account_id = :accountId OR receiver_account_id = :accountId AND created_at between :startDate AND :endDate order by created_at DESC", nativeQuery = true)
    Page<Transaction> historyByAccountId(@Param("accountId") Long accountId, @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate, Pageable pageable);
}
