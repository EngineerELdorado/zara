package com.zara.Zara.resources;

import com.zara.Zara.converters.UserResourceConverter;
import com.zara.Zara.dtos.requests.LoginRequest;
import com.zara.Zara.dtos.requests.OnboardingRequest;
import com.zara.Zara.dtos.requests.UserRegistrationRequest;
import com.zara.Zara.dtos.responses.UserResponse;
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
    public UserResponse login(LoginRequest loginRequest) {

        return userResourceConverter.convert(userService.login(loginRequest));
    }

    @Transactional
    public UserResponse onboard(Long userId, OnboardingRequest onboardingRequest) {
        return userResourceConverter.convert(userService.onboard(userId, onboardingRequest));
    }
}
