package com.zara.Zara.models;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Stat {

    private Long businesses;
    private Long customers;
    private Long agents;
    private Long transactions;
    private BigDecimal amount;
    private BigDecimal commissions;
    
}
