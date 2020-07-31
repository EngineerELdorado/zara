package com.zara.Zara.repositories;

import com.zara.Zara.entities.CommissionAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommissionAccountRepository extends JpaRepository<CommissionAccount, Long> {

    @Query(value = "SELECT * FROM commission_accounts WHERE main =:isMain FOR UPDATE ", nativeQuery = true)
    Optional<CommissionAccount> findMainCommissionAccountForUpdate(@Param("isMain") boolean isMain);

    @Query(value = "SELECT * FROM commission_accounts WHERE account_id =:prepaidAccountId FOR UPDATE ", nativeQuery = true)
    Optional<CommissionAccount> findByPrepaidAccountIdForUpdate(Long prepaidAccountId);

    Optional<CommissionAccount> findByAccountId(Long id);
}
