package com.zara.Zara.repositories;

import com.zara.Zara.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "select * from TRANSACTIONS where TRANSACTION_NUMBER=?1", nativeQuery = true)
    Transaction findByTransactionNumber(String transationNumber);
    @Query(value = "select * from TRANSACTIONS where SENDER=?1 or RECEIVER=?1 order by id DESC limit 50", nativeQuery = true)
    Collection<Transaction>getMiniStatement(Long userId);
    @Query(value = "select * from TRANSACTIONS order by ID desc ", nativeQuery = true)
    Collection<Transaction>getAll();
}
