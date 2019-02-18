package com.zara.Zara.models;

import lombok.Data;

@Data
public class TransactionRequestBody {

    private String sender;
    private String receiver;
    private String pin;
    private String description;
    private String amount;
    private String apiKey;

}
