package com.zara.Zara.services;

import com.zara.Zara.entities.Setting;

public interface ISettingsService {

    Setting save(Setting setting);
    Setting findSettings();
}
