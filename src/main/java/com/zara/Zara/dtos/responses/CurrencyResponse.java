package com.zara.Zara.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CurrencyResponse {

    private String code;
    private String name;
    private BigDecimal rate;
    private String symbol;
}
