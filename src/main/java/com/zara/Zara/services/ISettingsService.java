package com.zara.Zara.services;

import com.zara.Zara.entities.PesaPay;

public interface ISettingsService {

    PesaPay save(PesaPay pesaPay);
    PesaPay findSettings();
}
