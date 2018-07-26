package com.zara.Zara.services;

import com.zara.Zara.entities.Setting;

public interface ISettingService {

    public Setting getSettingById(Long id);
    public Setting add(Setting setting);

}
