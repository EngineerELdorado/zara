package com.zara.Zara.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponse {

    private Long accountId;
    private String accountNumber;
    private String accountHolder;
    private String email;
    private String phone;
    private String accountType;
    private String contact;
    private BigDecimal balance;
    private BigDecimal commissions;
}
