package com.zara.Zara.services;

import com.zara.Zara.entities.CommissionSetting;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface ICommissionSettingService {

     CommissionSetting save(CommissionSetting commissionSetting);
     Page<CommissionSetting>findAll(int page, int size);
     Page<CommissionSetting>filter(int page, int size, String param);
     CommissionSetting findOne(Long id);
     BigDecimal getCommission(BigDecimal amount);
}
