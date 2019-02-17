package com.zara.Zara.models;

import lombok.Data;

@Data
public class TransactionRequestBody {

    private String sender;
    private String receiver;
    private String agentNumber;
    private String businessNumber;
    private String pin;
    private String description;
    private String amount;

}
