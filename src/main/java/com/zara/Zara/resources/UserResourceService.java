package com.zara.Zara.resources;

import com.zara.Zara.dtos.requests.*;
import com.zara.Zara.dtos.responses.AccountResource;
import com.zara.Zara.dtos.responses.UserLoginResponse;
import com.zara.Zara.dtos.responses.UserProfileResponse;
import com.zara.Zara.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor
public class UserResourceService {

    private final UserService userService;

    @Transactional
    public void createUser(UserRegistrationRequest userRegistrationRequest) {

        userService.createUser(userRegistrationRequest);
    }


    public String forgotPassword(ForgotPasswordRequest request) {

       return userService.forgotPassword(request);
    }

    public String resetPassword(String passwordResetToken, ResetPasswordRequest request) {
       return userService.resetPassword(passwordResetToken, request);
    }
}
