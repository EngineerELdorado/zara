package com.zara.Zara.models;

import lombok.Data;

@Data
public class OnlinePaymentRequest {

    private String sender;
    private String receiver;
    private String otp;
    private String description;
    private String amount;
    private String apiKey;



}
