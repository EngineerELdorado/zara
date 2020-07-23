package com.zara.Zara.resources;

import com.zara.Zara.converters.UserResourceConverter;
import com.zara.Zara.dtos.requests.LoginRequest;
import com.zara.Zara.dtos.requests.OnboardingRequest;
import com.zara.Zara.dtos.requests.UserRegistrationRequest;
import com.zara.Zara.dtos.responses.OnboardingResponse;
import com.zara.Zara.dtos.responses.UserLoginResponse;
import com.zara.Zara.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor
public class UserResourceService {

    private final UserService userService;
    private final UserResourceConverter userResourceConverter;

    @Transactional
    public void createUser(UserRegistrationRequest userRegistrationRequest) {

        userService.createUser(userRegistrationRequest);
    }

    @Transactional
    public UserLoginResponse login(LoginRequest loginRequest) {

        return userResourceConverter.convertLoginResponse(userService.login(loginRequest));
    }

    @Transactional
    public OnboardingResponse onboard(Long userId, OnboardingRequest onboardingRequest) {
        return userResourceConverter.convertOnboardingResponse(userService.onboard(userId, onboardingRequest));
    }
}
