package com.zara.Zara.repositories;

import com.zara.Zara.entities.CommissionSetting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface CommissionSettingsRepository extends JpaRepository<CommissionSetting, Long> {

    @Override
    @Query(value = "select * from commission_settings order by commission", nativeQuery = true)
    Page<CommissionSetting> findAll(Pageable pageable);

    @Query(value = "select commission from commission_settings where ceil >=?1 and top <=?1", nativeQuery = true)
    BigDecimal getCommission(BigDecimal amount);
}
