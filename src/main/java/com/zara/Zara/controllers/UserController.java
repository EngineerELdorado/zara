package com.zara.Zara.controllers;

import com.zara.Zara.models.AppUser;
import com.zara.Zara.models.Role;
import com.zara.Zara.services.IRoleService;
import com.zara.Zara.services.IUserService;
import com.zara.Zara.utils.AfricasTalkingSms;
import com.zara.Zara.utils.GenerateRandomStuff;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import static com.zara.Zara.constants.ConstantVariables.ROLE_AGENT;
import static com.zara.Zara.constants.Keys.*;
import static com.zara.Zara.constants.Responses.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    IUserService userService;
    @Autowired
    IRoleService roleService;
    String userRole;
    Logger LOGGER = LogManager.getLogger(UserController.class);

    @PostMapping("/add")
    public ResponseEntity<?>addUser(@RequestBody AppUser appUser,
                                    HttpServletRequest request,
                                    HttpServletResponse response){


           if(isPhoneNumberValid(appUser.getPhone())){
               if(!isPhoneNumberTaken(appUser.getPhone())){
                   userRole = request.getHeader("role");
                   Collection<Role>roles = new ArrayList<>();
                   roles.add(roleService.getByName(userRole));
                   appUser.setBalance(0D);
                   appUser.setRoles(roles);
                   if(request.getHeader("role").equals(ROLE_AGENT)){
                       appUser.setAgentNumber(generateAgentNumber());
                   }
                   appUser.setCreatedOn(new Date());
                   appUser.setVerificationCode(String.valueOf(GenerateRandomStuff.getRandomNumber(5000)));
                   appUser.setAccountNumber(generateAccountNumber());
                   appUser.setPin(bCryptPasswordEncoder.encode(appUser.getPin()));
                   AppUser addedUser = userService.addUser(appUser);
                   response.addHeader(RESPONSE_CODE,RESPONSE_SUCCESS);
                   response.addHeader(RESPONSE_MESSAGE, USER_REGISTRATION_SUCCESS);

                   //TwilioSms.sendSMS(addedUser.getPhone(), "Your verification code is "+addedUser.getVerificationCode());
                   AfricasTalkingSms.sendSms(addedUser.getPhone(), YOUR_VERIFICATION_CODE_IS+" "+addedUser.getVerificationCode());
                   return ResponseEntity.status(201).body(userService.addUser(addedUser));
               }else{
                   response.addHeader(RESPONSE_CODE,RESPONSE_FAILURE);
                   response.addHeader(RESPONSE_MESSAGE, PHONE_NUMBER_ALREADY_TAKEN);
                   return null;
               }
           }
           else{
               response.addHeader(RESPONSE_CODE,RESPONSE_FAILURE);
               response.addHeader(RESPONSE_MESSAGE, PHONE_NUMBER_FORMAT_INVALID);
               return null;
           }

    }

    @GetMapping("/verifyAccount")
    public void veryAccount(@RequestParam String accountNumber,
                            @RequestParam String verificationCode,
                            HttpServletResponse response){

        AppUser appUser = userService.findByAccountNumber(accountNumber);
        if(appUser.getVerificationCode().equals(verificationCode)){
            appUser.setVerified(true);
            appUser.setVerificationCode(null);
            userService.addUser(appUser);
            response.addHeader(RESPONSE_CODE, RESPONSE_SUCCESS);
            response.addHeader(RESPONSE_MESSAGE, VERIFICATION_CODE_SUCCESS);
        }
        else{
            response.addHeader(RESPONSE_CODE, RESPONSE_SUCCESS);
            response.addHeader(RESPONSE_MESSAGE, VERIFICATION_CODE_FAILURE);
        }

    }
    @GetMapping("/resendVerificationCode/{accountNumber}")
    public void resendVerificationCode(@PathVariable String accountNumber){
        AppUser appUser = userService.findByAccountNumber(accountNumber);
        appUser.setVerificationCode(String.valueOf(GenerateRandomStuff.getRandomNumber(50000)));
        AppUser updatedUser =  userService.addUser(appUser);
        LOGGER.info("RESENT VERIFICATION CODE "+updatedUser.getVerificationCode());

    }

    @GetMapping("/getAll")
    public ResponseEntity<?>getAll(){
        return ResponseEntity.status(200).body(userService.getAll());
    }

    @GetMapping("/getByAccountNumber/{accountNumber}")
    public ResponseEntity<?>getByAccountNumber(@PathVariable String accountNumber, HttpServletResponse response){

        Optional<AppUser>appUser = Optional.of(userService.findByAccountNumber(accountNumber));
        if(appUser.isPresent()){
            return ResponseEntity.status(200).body(appUser.get());
        }
        else{
            response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
            response.addHeader(RESPONSE_MESSAGE, USER_NOT_FOUND);
            return ResponseEntity.status(404).body(USER_NOT_FOUND);
        }
    }

    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<?>getUserBalance(@PathVariable String accountNumber){

        return ResponseEntity.status(200).body(userService.getUserBalance(accountNumber));
    }




    public String generateAccountNumber(){
        String accountNumber = ACCOUNT_NUMBER_PREFIX+String.valueOf(GenerateRandomStuff.getRandomNumber(1000000));
        AppUser appUser = userService.findByAccountNumber(accountNumber);
        if(appUser==null){
            return accountNumber;

        }
        else{
            generateAccountNumber();
        }
        return null;
    }

    public String generateAgentNumber(){
        String agentNumber = ACCOUNT_NUMBER_PREFIX+String.valueOf(GenerateRandomStuff.getRandomNumber(10000));
        Optional<AppUser> appUser = userService.findByAgentNumber(agentNumber);
        if(!appUser.isPresent()){
            return agentNumber;

        }
        else{
            generateAgentNumber();
        }
        return null;
    }

    public boolean isPhoneNumberTaken(String phoneNumber){
        Optional<AppUser> user = userService.findByPhoneNumber(phoneNumber);
        if(user.isPresent()){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isPhoneNumberValid(String phoneNumber){
        if (phoneNumber.startsWith("+") && phoneNumber.length()>10 && phoneNumber.length()==13){
            return true;
        }
        else{
            return false;
        }
    }


}
