package com.zara.Zara.models;

import lombok.Data;

@Data
public class SafepayDto {

    private String merchantRefNum;
    private String amount;
    private String accountHolderName;
    private String accountType;
    private String accountNumber;
    private String routingNumber;
    private String payMethod;
}
