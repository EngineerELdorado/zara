package com.zara.Zara.models;

import lombok.Data;

@Data
public class OtpObject {

    private String phoneNumber;
    private int otp;
    private String apiKey;
    private String email;
}
