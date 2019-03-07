package com.zara.Zara.services;

import com.zara.Zara.entities.PesaPay;
import com.zara.Zara.repositories.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingServiceimp implements ISettingsService {

    @Autowired
    SettingRepository settingRepository;
    @Override
    public PesaPay save(PesaPay pesaPay) {
        return settingRepository.save(pesaPay);
    }

    @Override
    public PesaPay findSettings() {
        return settingRepository.findSettings();
    }
}
