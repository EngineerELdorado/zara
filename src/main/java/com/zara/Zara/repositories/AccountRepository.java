package com.zara.Zara.repositories;

import com.zara.Zara.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findByUserId(Long userId);

    @Query(value = "SELECT * FROM accounts WHERE account_number =:accountNumber FOR UPDATE", nativeQuery = true)
    Optional<Account> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);

    @Query(value = "SELECT * FROM accounts WHERE id =:id FOR UPDATE", nativeQuery = true)
    Optional<Account> findByIdForUpdate(@Param("id") Long id);

    @Query(value = "SELECT * FROM accounts WHERE user_id =:userId FOR UPDATE", nativeQuery = true)
    Optional<Account> findByUserIdFofUpdate(Long userId);
}
