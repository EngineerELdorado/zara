package com.zara.Zara.services;

import com.zara.Zara.entities.Setting;
import com.zara.Zara.repositories.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingServiceImpl implements ISettingService {

    @Autowired
    SettingRepository settingRepository;
    @Override
    public Setting getSettingById(Long id) {
        return settingRepository.getOne(id);
    }

    @Override
    public Setting add(Setting setting) {
        return settingRepository.save(setting);
    }
}
