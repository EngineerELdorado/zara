package com.zara.Zara.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserProfileResponse {

    private Long userId;
    private String accountType;
    private String accountNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String profilePic;
    private boolean isOnboarded;
    private BigDecimal balance;
    private BigDecimal commissions;
    private String accountCurrency;

}
