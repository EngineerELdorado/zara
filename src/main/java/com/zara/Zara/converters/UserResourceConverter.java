package com.zara.Zara.converters;

import com.zara.Zara.dtos.responses.AccountResource;
import com.zara.Zara.dtos.responses.UserLoginResponse;
import com.zara.Zara.dtos.responses.UserProfileResponse;
import com.zara.Zara.entities.Account;
import com.zara.Zara.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserResourceConverter {

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

        return UserProfileResponse.builder()
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .email(profile.getEmail())
                .phone(profile.getPhoneNumber())
                .profilePic("")
                .userId(profile.getId())
                .isOnboarded(profile.isOnboarded())
                .build();
    }
}
