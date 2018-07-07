package com.zara.Zara.repositories;

import com.zara.Zara.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "select * from transaction where transaction_number=?1", nativeQuery = true)
    public Transaction findByTransactionNumber(String transationNumber);


}
