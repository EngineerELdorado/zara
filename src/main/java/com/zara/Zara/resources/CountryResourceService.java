package com.zara.Zara.resources;

import com.zara.Zara.converters.CountryResourceConverter;
import com.zara.Zara.dtos.responses.CountryResponse;
import com.zara.Zara.services.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CountryResourceService {

    private final CountryResourceConverter countryResourceConverter;
    private final CountryService countryService;

    public List<CountryResponse> findAll() {

        return countryService.findAll().stream().map(countryResourceConverter::convert)
                .collect(Collectors.toList());
    }
}
