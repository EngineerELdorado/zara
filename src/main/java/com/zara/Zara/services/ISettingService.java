package com.zara.Zara.services;

import com.zara.Zara.entities.Setting;

import java.util.Collection;

public interface ISettingService {

    public Setting getSettingById(Long id);
    public Setting add(Setting setting);
    public Collection<Setting>allSettings();

}
