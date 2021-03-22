package com.zara.Zara.dtos.requests;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConversionRequest {

    private BigDecimal amount;
    private String fromCurrencyCode;
    private String toCurrencyCode;
}
