package com.zara.Zara.controllers;

import com.zara.Zara.entities.AppUser;
import com.zara.Zara.entities.Role;
import com.zara.Zara.services.IRoleService;
import com.zara.Zara.services.IUserService;
import com.zara.Zara.utils.GenerateRandomStuff;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import static com.zara.Zara.utils.BusinessNumbersGenerator.generateAccountNumber;
import static com.zara.Zara.utils.BusinessNumbersGenerator.generateAgentNumber;
import static com.zara.Zara.utils.CheckingUtils.isPhoneNumberTaken;
import static com.zara.Zara.utils.CheckingUtils.isPhoneNumberValid;
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
    HttpHeaders responseHeaders = new HttpHeaders();
    Logger LOGGER = LogManager.getLogger(UserController.class);

    @PostMapping("/add")
    public ResponseEntity<?>addUser(@RequestBody AppUser appUser,
                                    @RequestParam String role,
                                    @RequestParam(required = false) String generatePin){


           if(isPhoneNumberValid(appUser.getPhone())){

               if(!isPhoneNumberTaken(appUser.getPhone().substring(1),userService)){
                   userRole = role;
                   Collection<Role>roles = new ArrayList<>();
                   roles.add(roleService.getByName(userRole));
                   appUser.setPhone(appUser.getPhone().substring(1));
                   appUser.setBalance(0D);
                   appUser.setRoles(roles);
                   if(role.equals(ROLE_AGENT)){
                       appUser.setAgentNumber(generateAgentNumber(userService));
                   }
                   appUser.setCreatedOn(new Date());
                   appUser.setVerificationCode(String.valueOf(GenerateRandomStuff.getRandomNumber(5000)));
                   appUser.setAccountNumber(generateAccountNumber(userService));
                   if(generatePin!=null && generatePin.equals("true")){
                       String tempPin =String.valueOf(GenerateRandomStuff.getRandomNumber(1000));
                       appUser.setPin(bCryptPasswordEncoder.encode(tempPin));
                       appUser.setNeedToChangePin(true);
                       appUser.setTempPin(tempPin);
                   }
                   else{
                       appUser.setPin(bCryptPasswordEncoder.encode(appUser.getPin()));
                   }

                   AppUser addedUser = userService.addUser(appUser);

                   //TwilioSms.sendSMS(addedUser.getPhone(), "Your verification code is "+addedUser.getVerificationCode());
                   // TODO: 07/07/2018 SEND VERIFICATION CODE VIA SMS
                   responseHeaders.set(RESPONSE_CODE,RESPONSE_SUCCESS);
                   responseHeaders.set(RESPONSE_MESSAGE, USER_REGISTRATION_SUCCESS+" "+appUser.getFullName());
                   return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
               }

                   else{
                   responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                   responseHeaders.set(RESPONSE_MESSAGE, PHONE_NUMBER_ALREADY_TAKEN);
                   return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
               }
           }
           else{
               responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
               responseHeaders.set(RESPONSE_MESSAGE, PHONE_NUMBER_FORMAT_INVALID);
               return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
           }

    }

    @GetMapping("/verifyAccount")
    public ResponseEntity<?> veryAccount(
                            @RequestParam String accountNumber,
                            @RequestParam String verificationCode,
                            HttpServletResponse response){

        AppUser appUser = userService.findByAccountNumber(accountNumber);
        if(appUser.getVerificationCode().equals(verificationCode)){
            appUser.setVerified(true);
            appUser.setVerificationCode(null);
            userService.addUser(appUser);
            responseHeaders.set(RESPONSE_MESSAGE, VERIFICATION_CODE_SUCCESS);
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        }
        else{
            responseHeaders.set(RESPONSE_MESSAGE, VERIFICATION_CODE_FAILURE);
            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
        }

    }
    @GetMapping("/resendVerificationCode/{accountNumber}")
    public ResponseEntity<?> resendVerificationCode(@PathVariable String accountNumber){
        AppUser appUser = userService.findByAccountNumber(accountNumber);
        appUser.setVerificationCode(String.valueOf(GenerateRandomStuff.getRandomNumber(50000)));
        AppUser updatedUser =  userService.addUser(appUser);
        responseHeaders.set(RESPONSE_MESSAGE, VERIFICATION_CODE_RESENT+" :+"+updatedUser.getPhone());
        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);

    }

    @GetMapping("/getAll")
    public ResponseEntity<?>getAll(){
        return ResponseEntity.status(200).body(userService.getAll());
    }

    @GetMapping("/getByAccountNumber/{accountNumber}")
    public ResponseEntity<?>getByAccountNumber(@PathVariable String accountNumber, HttpServletResponse response){

        Optional<AppUser>appUser = Optional.of(userService.findByAccountNumber(accountNumber));
        if(appUser.isPresent()){
            return ResponseEntity.status(200).header(RESPONSE_MESSAGE, "User Found").body(appUser.get());
        }
        else{
            return ResponseEntity.status(404).header(RESPONSE_MESSAGE,USER_NOT_FOUND).body(null);

        }
    }

    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<?>getUserBalance(@PathVariable String accountNumber){

        return ResponseEntity.status(200).body(userService.getUserBalance(accountNumber));
    }

    @GetMapping("/lockUnlock")
    public ResponseEntity<?> lockOrUnlockAccount(@RequestParam String accountNumber,@RequestParam String doneBy){
        AppUser user = userService.findByAccountNumber(accountNumber);
        AppUser by = userService.findByAccountNumber(doneBy);
        if(user.isLocked){
            user.setLocked(false);
            user.setUnlockedBy(by);
            user.setLockedOn(new Date());
            responseHeaders.set(RESPONSE_CODE,RESPONSE_SUCCESS);
            responseHeaders.set(RESPONSE_MESSAGE, BLOCK_USER+" "+user.getFullName());
            LOGGER.info("ACCOUNT UNLOCKED");
        }
        else{
            user.setLocked(true);
            user.setLockedBy(by);
            user.setUnlockedOn(new Date());

            responseHeaders.set(RESPONSE_CODE,RESPONSE_SUCCESS);
            responseHeaders.set(RESPONSE_MESSAGE, UNBLOCK_USER+" "+user.getFullName());
            LOGGER.info("ACCOUNT LOCKED");
        }
        if(!user.getAccountNumber().equals(by.getAccountNumber()))
        {
            userService.addUser(user);
        }
        else{

            responseHeaders.set(RESPONSE_MESSAGE, CANNOT_BLOCK_YOURSELF);
            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    }

    @PostMapping("/update/{accountNumber}")
    public ResponseEntity<?>updateUserDetails(@RequestBody AppUser appUser,@PathVariable String accountNumber){

        AppUser existingUser = userService.findByAccountNumber(accountNumber);
        existingUser.setFullName(appUser.getFullName());
        userService.addUser(existingUser);
        responseHeaders.set(RESPONSE_MESSAGE, USER_UPDATED);
        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    }
    @GetMapping("/checkVerified/{accountNumber}")
    public ResponseEntity<?>isVerified(@PathVariable String accountNumber){
        AppUser user = userService.findByAccountNumber(accountNumber);
        if (user.isVerified()){
            responseHeaders.set(RESPONSE_MESSAGE, "yes");
        }
        else{
            responseHeaders.set(RESPONSE_MESSAGE, "no");
        }

        return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    }
}
