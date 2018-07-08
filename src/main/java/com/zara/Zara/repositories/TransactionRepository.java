package com.zara.Zara.repositories;

import com.zara.Zara.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "select * from transaction where transaction_number=?1", nativeQuery = true)
    Transaction findByTransactionNumber(String transationNumber);
    @Query(value = "select * from transaction where created_by_id=?1 or receiver_id=?1 order by id DESC limit 50", nativeQuery = true)
    Collection<Transaction>getMiniStatement(Long userId);
}
