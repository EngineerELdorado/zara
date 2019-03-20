package com.zara.Zara.models;

import lombok.Data;

@Data
public class PaymentSetting {

    private String airtelMoneyNumber;
    private String orangeMoneyNumber;
    private String mpesaNmumber;
    private String bankAccountNumber;
    private String bankAccountName;
    private String bankAccountHolder;
    private String bankAccountSwift;
    private String bankAccountCountry;
    private String paypalEmail;
    private String pin;
}
