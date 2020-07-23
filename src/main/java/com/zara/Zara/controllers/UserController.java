package com.zara.Zara.controllers;

import com.zara.Zara.dtos.requests.LoginRequest;
import com.zara.Zara.dtos.requests.OnboardingRequest;
import com.zara.Zara.dtos.requests.UserRegistrationRequest;
import com.zara.Zara.dtos.responses.OnboardingResponse;
import com.zara.Zara.dtos.responses.UserLoginResponse;
import com.zara.Zara.resources.UserResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserResourceService userResourceService;

    @PostMapping("")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest) {

        userResourceService.createUser(userRegistrationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {

        UserLoginResponse userLoginResponse = userResourceService.login(loginRequest);
        return new ResponseEntity<>(userLoginResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/{userId}/onboard", headers = "Authorization")
    public ResponseEntity<OnboardingResponse> onboardUser(@PathVariable Long userId, @Valid @RequestBody OnboardingRequest onboardingRequest) {

        OnboardingResponse onboardingResponse = userResourceService.onboard(userId, onboardingRequest);
        return new ResponseEntity<>(onboardingResponse, HttpStatus.OK);
    }
}
