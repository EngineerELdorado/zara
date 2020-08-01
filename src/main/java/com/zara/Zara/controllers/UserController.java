package com.zara.Zara.controllers;

import com.zara.Zara.dtos.requests.*;
import com.zara.Zara.dtos.responses.AccountResource;
import com.zara.Zara.dtos.responses.UserLoginResponse;
import com.zara.Zara.dtos.responses.UserProfileResponse;
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
    public ResponseEntity<AccountResource> onboardUser(@PathVariable Long userId, @Valid @RequestBody OnboardingRequest onboardingRequest) {

        AccountResource accountResource = userResourceService.onboard(userId, onboardingRequest);
        return new ResponseEntity<>(accountResource, HttpStatus.OK);
    }

    @GetMapping(value = "/profile/{userId}", headers = "Authorization")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable Long userId) {

        return new ResponseEntity<>(userResourceService.getProfile(userId), HttpStatus.OK);
    }

    @PostMapping(value = "/{userId}/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {

        return new ResponseEntity<>(userResourceService.forgotPassword(request), HttpStatus.OK);
    }

    @PostMapping(value = "/{userId}/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String passwordResetToken, @RequestBody ResetPasswordRequest request) {

        return new ResponseEntity<>(userResourceService.resetPassword(passwordResetToken, request), HttpStatus.OK);
    }
}
