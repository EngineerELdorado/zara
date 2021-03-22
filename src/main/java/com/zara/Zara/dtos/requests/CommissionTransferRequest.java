package com.zara.Zara.dtos.requests;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CommissionTransferRequest {

    private BigDecimal amount;
}
