package com.zara.Zara.resources;

import com.zara.Zara.converters.CountryResourceConverter;
import com.zara.Zara.converters.CurrencyResourceConverter;
import com.zara.Zara.dtos.responses.CountryResponse;
import com.zara.Zara.dtos.responses.CurrencyResponse;
import com.zara.Zara.services.CountryService;
import com.zara.Zara.services.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CurrencyResourceService {

    private final CurrencyResourceConverter currencyResourceConverter;
    private final CurrencyService currencyService;

    public List<CurrencyResponse> findAll() {

        return currencyService.findAll().stream().map(currencyResourceConverter::convert)
                .collect(Collectors.toList());
    }
}
