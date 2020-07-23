package com.zara.Zara.controllers;

import com.zara.Zara.dtos.requests.LoginRequest;
import com.zara.Zara.dtos.requests.OnboardingRequest;
import com.zara.Zara.dtos.requests.UserRegistrationRequest;
import com.zara.Zara.dtos.responses.UserResponse;
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
    public ResponseEntity<?> createUser(@Valid UserRegistrationRequest userRegistrationRequest) {

        userResourceService.createUser(userRegistrationRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> loginUser(@Valid LoginRequest loginRequest) {

        UserResponse userResponse = userResourceService.login(loginRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PostMapping("/{userId}/onboard")
    public ResponseEntity<UserResponse> onboardUser(@PathVariable Long userId, @Valid OnboardingRequest onboardingRequest) {

        UserResponse userResponse = userResourceService.onboard(userId, onboardingRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }
}
