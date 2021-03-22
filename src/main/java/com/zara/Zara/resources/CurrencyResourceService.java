package com.zara.Zara.resources;

import com.zara.Zara.converters.CurrencyResourceConverter;
import com.zara.Zara.dtos.requests.ConversionRequest;
import com.zara.Zara.dtos.responses.CurrencyResponse;
import com.zara.Zara.services.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public BigDecimal convert(ConversionRequest request) {
        return currencyService.convert(request.getFromCurrencyCode(), request.getToCurrencyCode(), request.getAmount(),
                2, RoundingMode.HALF_UP);
    }
}
