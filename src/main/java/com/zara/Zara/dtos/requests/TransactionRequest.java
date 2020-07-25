package com.zara.Zara.dtos.requests;

import com.zara.Zara.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {

    private String recipient;
    private BigDecimal amount;
    private TransactionType transactionType;
}
