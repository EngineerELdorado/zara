package com.zara.Zara.services;

import com.zara.Zara.entities.Developer;
import com.zara.Zara.repositories.DeveloperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeveloperServiceImp implements IDeveloperService {
    @Autowired
    DeveloperRepository developerRepository;

    @Override
    public Developer save(Developer developer) {
        return developerRepository.save(developer);
    }

    @Override
    public Developer findByApiKey(String apiKey) {
        return developerRepository.findByApiKey(apiKey);
    }
}
