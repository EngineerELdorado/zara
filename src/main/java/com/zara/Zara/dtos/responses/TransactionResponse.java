package com.zara.Zara.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class TransactionResponse {

    private Long transactionId;
    private String transactionNumber;
    private Date createdAt;
    private AccountResponse sender;
    private AccountResponse recipient;
    private String type;
    private BigDecimal senderAmount;
    private BigDecimal senderAmountInUsd;
    private BigDecimal senderAmountInReceiverCurrency;
    private String senderCurrency;
    private BigDecimal chargesInSenderCurrency;
    private BigDecimal chargesInUsd;
    private BigDecimal chargesInReceiverCurrency;
    private BigDecimal receiverAmount;
    private BigDecimal receiverAmountInUsd;
    private BigDecimal receiverAmountInSenderCurrency;
    private String receiverCurrency;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private BigDecimal exchangeRate;
    private String status;
}
