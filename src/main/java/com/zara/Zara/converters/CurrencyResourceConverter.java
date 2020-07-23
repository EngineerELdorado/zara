package com.zara.Zara.converters;

import com.zara.Zara.dtos.responses.CurrencyResponse;
import com.zara.Zara.entities.Currency;
import org.springframework.stereotype.Component;

@Component
public class CurrencyResourceConverter {

    public CurrencyResponse convert(Currency currency) {

        return CurrencyResponse.builder()
                .code(currency.getCode())
                .name(currency.getName())
                .build();
    }
}
