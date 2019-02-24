package com.zara.Zara.models;

import lombok.Data;

@Data
public class LoginObject {

    private String phoneNumber;
    private String pin;
    private String agentNumber;
    private String businessNumber;
    private String username;
    private String password;
}
