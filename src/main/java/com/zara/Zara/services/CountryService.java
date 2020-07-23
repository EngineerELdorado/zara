package com.zara.Zara.services;

import com.zara.Zara.entities.Country;
import com.zara.Zara.repositories.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public List<Country> findAll(){
        return countryRepository.findAll();
    }
}
