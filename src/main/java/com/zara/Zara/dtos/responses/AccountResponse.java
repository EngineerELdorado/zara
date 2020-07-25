package com.zara.Zara.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountResponse {

    private Long accountId;
    private String accountNumber;
    private String accountHolder;
    private String email;
    private String phone;
}
