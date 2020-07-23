package com.zara.Zara.services;

import com.zara.Zara.configs.security.JwtUtil;
import com.zara.Zara.dtos.requests.LoginRequest;
import com.zara.Zara.dtos.requests.OnboardingRequest;
import com.zara.Zara.dtos.requests.UserRegistrationRequest;
import com.zara.Zara.entities.Account;
import com.zara.Zara.entities.Country;
import com.zara.Zara.entities.Currency;
import com.zara.Zara.entities.User;
import com.zara.Zara.exceptions.exceptions.Zaka400Exception;
import com.zara.Zara.exceptions.exceptions.Zaka500Exception;
import com.zara.Zara.repositories.AccountRepository;
import com.zara.Zara.repositories.CountryRepository;
import com.zara.Zara.repositories.CurrencyRepository;
import com.zara.Zara.repositories.UserRepository;
import com.zara.Zara.services.mail.EmailService;
import com.zara.Zara.utils.BusinessNumbersGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CountryRepository countryRepository;
    private final CurrencyRepository currencyRepsotory;
    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;
    @Qualifier("userDetailsServiceImp")
    private final UserDetailsService userDetailsService;

    @Transactional
    public void createUser(UserRegistrationRequest userRegistrationRequest) {

        User user = new User();
        user.setEmail(userRegistrationRequest.getEmail());
        user.setFirstName(userRegistrationRequest.getFirstName());
        user.setLastName(userRegistrationRequest.getLastName());
        user.setPassword(passwordEncoder.encode(userRegistrationRequest.getPassword()));

        if (userRepository.findByEmail(userRegistrationRequest.getEmail()).isPresent()) {
            throw new Zaka400Exception("Email already in use");
        }

        try {
            userRepository.save(user);
            String welcomeMsg = user.getFirstName() + " Welcome to PesaPay";
            emailService.sendEmail("torapos@gmail.com", user.getEmail(), "User registration", welcomeMsg);
        } catch (Exception e) {
            log.info("User creation failed. Possible cause: " + e.getCause());
            throw new Zaka500Exception("Account creation failed. Please try again or contact support");
        }
    }

    @Transactional
    public User login(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getUsername()).orElseThrow(() ->
                new Zaka400Exception("Wrong username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new Zaka400Exception("Wrong username or password");
        }
        String token = jwtUtil.generateToken(this.userDetailsService.loadUserByUsername(user.getEmail()));
        user.setToken(token);
        return user;
    }

    @Transactional
    public Account onboard(Long userId, OnboardingRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Zaka400Exception("User not found"));
        if (userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new Zaka400Exception("Phone number already taken");
        }
        Currency currency = currencyRepsotory.findByCode(request.getCurrencyCode())
                .orElseThrow(() -> new Zaka400Exception("Currency not found"));
        Country country = countryRepository.findByCode(request.getCountryCode())
                .orElseThrow(() -> new Zaka400Exception("Currency not found"));
        Account account = new Account();
        account.setAccountNumber(BusinessNumbersGenerator.generateAccountNumber(accountRepository));
        account.setCurrency(currency);
        account.setBalance(BigDecimal.ZERO);
        account.setUser(user);
        account.setPin(passwordEncoder.encode(request.getPin()));
        account.setCountry(country);
        account.setType(request.getAccountType());

        try {
            accountRepository.save(account);
            user.setPhoneNumber(request.getPhoneNumber());
            user.setOnboarded(true);
            userRepository.save(user);
            String emailMsg = "Congratulations...Your account has been fully setup. \n" +
                    "Account holder: " + user.getFirstName() + " " + user.getLastName() + "\n" +
                    "Account number: " + account.getAccountNumber() + "\n" +
                    "Balance " + account.getBalance() + "\n" +
                    "Currency " + account.getCurrency().getCode();
            emailService.sendEmail("toraposltd@gmail.com", user.getEmail(), "Account Setup", emailMsg);
        } catch (Exception e) {
            log.error("Failed to create account. Possible cause: " + e.getCause());
            throw new Zaka500Exception("Account Creation Failed");
        }
        return account;
    }
}
