package com.zara.Zara.repositories;

import com.zara.Zara.entities.BalanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceLogRepository extends JpaRepository<BalanceLog, Long> {

    @Query(value = "SELECT * FROM balance_logs where transaction_id=:transactionId and account_id=:accountId",
            nativeQuery = true)
    Optional<BalanceLog> findByTransactionIdAndAccountId(@Param("transactionId") Long transactionId,
                                                         @Param("accountId") Long accountId);
}
