package com.zara.Zara.services;

import com.zara.Zara.entities.CommissionSetting;
import com.zara.Zara.repositories.CommissionSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommissionSettingServiceImp implements ICommissionSettingService {

    @Autowired
    CommissionSettingsRepository commissionSettingsRepository;

    @Override
    public CommissionSetting save(CommissionSetting commissionSetting) {
        return commissionSettingsRepository.save(commissionSetting);
    }

    @Override
    public Page<CommissionSetting> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return commissionSettingsRepository.findAll(pageable);
    }

    @Override
    public Page<CommissionSetting> filter(int page, int size, String param) {

        return null;
    }

    @Override
    public CommissionSetting findOne(Long id) {
        return commissionSettingsRepository.getOne(id);
    }
}
