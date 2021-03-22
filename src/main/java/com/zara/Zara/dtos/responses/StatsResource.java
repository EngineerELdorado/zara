package com.zara.Zara.dtos.responses;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatsResource {

    private Long id;
    private BigDecimal totalSent;
    private BigDecimal totalReceived;
    private BigDecimal balance;
    private Long numberOfSentTransactions;
    private Long numberOfReceivedTransactions;

}
