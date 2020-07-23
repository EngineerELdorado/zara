package com.zara.Zara.converters;

import com.zara.Zara.dtos.responses.CountryResponse;
import com.zara.Zara.entities.Country;
import org.springframework.stereotype.Component;

@Component
public class CountryResourceConverter {

    public CountryResponse convert(Country country) {

        return CountryResponse.builder()
                .code(country.getCode())
                .name(country.getName())
                .flag(country.getFlagUrl())
                .build();
    }
}
