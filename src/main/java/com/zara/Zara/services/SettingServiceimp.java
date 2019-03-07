package com.zara.Zara.services;

import com.zara.Zara.entities.Setting;
import com.zara.Zara.repositories.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingServiceimp implements ISettingsService {

    @Autowired
    SettingRepository settingRepository;
    @Override
    public Setting save(Setting setting) {
        return settingRepository.save(setting);
    }

    @Override
    public Setting findSettings() {
        return settingRepository.findSettings();
    }
}
