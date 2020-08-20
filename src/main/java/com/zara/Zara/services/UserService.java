package com.zara.Zara.services;

import com.zara.Zara.configs.security.JwtUtil;
import com.zara.Zara.dtos.requests.*;
import com.zara.Zara.entities.*;
import com.zara.Zara.exceptions.exceptions.Zaka400Exception;
import com.zara.Zara.exceptions.exceptions.Zaka500Exception;
import com.zara.Zara.repositories.*;
import com.zara.Zara.services.mail.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CurrencyRepository currencyRepsotory;
    private final JwtUtil jwtUtil;
    @Qualifier("userDetailsServiceImp")
    private final UserDetailsService userDetailsService;

    @Transactional
    public void createUser(UserRegistrationRequest userRegistrationRequest) {


    }

    @Transactional
    public Admin login(LoginRequest loginRequest) {

        Admin admin = userRepository.findByEmail(loginRequest.getUsername()).orElseThrow(() ->
                new Zaka400Exception("Wrong username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
            throw new Zaka400Exception("Wrong username or password");
        }
        String token = jwtUtil.generateToken(this.userDetailsService.loadUserByUsername(admin.getEmail()));
        admin.setToken(token);
        return admin;
    }

    public Admin getProfile(Long userId) {

        return userRepository.findById(userId).orElseThrow(() -> new Zaka400Exception("Admin not found for ID" + userId));
    }

    public String forgotPassword(ForgotPasswordRequest request) {

        Admin admin = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new Zaka500Exception("" +
                " Admin not found for email " + request.getEmail()));
        String resetPasswordToken = jwtUtil.generateToken(this.userDetailsService.loadUserByUsername(admin.getEmail()));

        try {
            userRepository.save(admin);
        } catch (Exception e) {
            log.error("Failed to save the resetPasswordToken. Possible cause: " + e.getCause());
            throw new Zaka500Exception("Account Creation Failed");
        }
        // TODO: 02/08/2020 Send reset password link with the token

        return "We have sent an email to your address to reset your password";
    }

    public String resetPassword(String passwordResetToken, ResetPasswordRequest request) {

        return "Your password has been reset successfully";
    }
}
