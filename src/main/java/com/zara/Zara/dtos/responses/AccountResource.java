package com.zara.Zara.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountResource {

    private Long accountId;
    private String accountNumber;
    private String accountType;
    private String currencyCode;
    private BigDecimal balance;
}
