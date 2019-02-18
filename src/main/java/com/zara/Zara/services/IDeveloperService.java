package com.zara.Zara.services;

import com.zara.Zara.entities.Developer;

public interface IDeveloperService {

    Developer save(Developer developer);
    Developer findByApiKey(String apiKey);
}
