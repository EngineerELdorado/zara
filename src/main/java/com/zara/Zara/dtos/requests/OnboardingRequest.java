package com.zara.Zara.dtos.requests;

import com.zara.Zara.enums.AccountType;
import lombok.Data;

@Data
public class OnboardingRequest {

    private AccountType accountType;
    //private String countryCode;
    private String currencyCode;
    private String pin;
}
