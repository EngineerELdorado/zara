package com.zara.Zara.models;

import lombok.Data;

@Data
public class OnlineOtpRequest {
    private String phoneNumber;
    private String apiKey;
}
