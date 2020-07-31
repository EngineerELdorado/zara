package com.zara.Zara.converters;

import com.zara.Zara.dtos.responses.AccountResource;
import com.zara.Zara.dtos.responses.UserLoginResponse;
import com.zara.Zara.dtos.responses.UserProfileResponse;
import com.zara.Zara.entities.Account;
import com.zara.Zara.entities.CommissionAccount;
import com.zara.Zara.entities.User;
import com.zara.Zara.exceptions.exceptions.Zaka400Exception;
import com.zara.Zara.repositories.AccountRepository;
import com.zara.Zara.repositories.CommissionAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserResourceConverter {

    private final AccountRepository accountRepository;
    private final CommissionAccountRepository commissionAccountRepository;

    public UserLoginResponse convertLoginResponse(User user) {
        return UserLoginResponse.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .onboarded(user.isOnboarded())
                .token(user.getToken())
                .build();
    }

    public AccountResource convertToAccountResource(Account account) {

        return AccountResource.builder()
                .accountId(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getType().name())
                .balance(account.getBalance())
                .currencyCode(account.getCurrency().getCode())
                .build();
    }

    public UserProfileResponse convertToProfileResource(User profile) {

        Account account = accountRepository.findByUserId(profile.getId())
                .orElseThrow(() -> new Zaka400Exception("Account not found"));
        CommissionAccount commissionAccount = commissionAccountRepository.findByAccountId(account.getId())
                .orElseThrow(() -> new Zaka400Exception("Account not found"));
        return UserProfileResponse.builder()
                .accountType(account.getType().name())
                .accountNumber(account.getAccountNumber())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .email(profile.getEmail())
                .phone(profile.getPhoneNumber())
                .profilePic("")
                .userId(profile.getId())
                .isOnboarded(profile.isOnboarded())
                .balance(account.getBalance())
                .commissions(commissionAccount.getBalance())
                .accountCurrency(account.getCurrency().getCode())
                .build();
    }
}
