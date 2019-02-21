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
    private String customerIp;
    private String firstName;
    private String lastName;
    private String email;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zip;
    private String phone;
}
