package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.models.OtpObject;
import com.zara.Zara.services.ICustomerService;
import com.zara.Zara.services.utils.OtpService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

import static com.zara.Zara.constants.Responses.PHONE_NUMBER_ALREADY_TAKEN;
import static com.zara.Zara.constants.Responses.USER_REGISTRATION_SUCCESS;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    ICustomerService customerService;
    @Autowired
    OtpService otpService;
    Logger LOG = LogManager.getLogger(CustomerController.class);
    ApiResponse apiResponse = new ApiResponse();
    @PostMapping("/post")
    public ResponseEntity<?> save(@RequestBody Customer customer){

        customer.setPin(bCryptPasswordEncoder.encode(customer.getPin()));
        customer.setCreationDate(new Date());
        customer.setVerified(false);
        customer.setStatus("UNVERIFIED");
        customer.setStatusDescription("the customer needs to verify his phone number");
        customer.setBalance(new BigDecimal("0"));
        if (!isPhoneTaken(customer.getPhoneNumber())){
            Customer savedCustomer = customerService.save(customer);
            if (savedCustomer!=null){
                apiResponse.setResponseCode("00");
                apiResponse.setResponseMessage(USER_REGISTRATION_SUCCESS);
                LOG.info("REGISTRATION SUCCESSFUL");
            }else{
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage(USER_REGISTRATION_SUCCESS);
                LOG.info("REGISTRATION FAILED. BAD REQUEST PROBABLY");
            }
        }else{
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage(PHONE_NUMBER_ALREADY_TAKEN);
            LOG.info("REGISTRATION FAILED. PHONE NUMBER ALREADY TAKEN");
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);

    }


    public boolean isPhoneTaken(String phoneNumber){
       Customer customer = customerService.findByPhoneNumber(phoneNumber);
       if (customer!=null){
           return true;
        }else {
           return false;
       }
    }

    @GetMapping("/generateOtp")
    public ResponseEntity<?> generateOtp(@RequestParam(name = "phoneNumber") String phoneNumber){
        int otp = otpService.generateOTP(phoneNumber);
         apiResponse.setResponseCode("00");
         apiResponse.setResponseMessage("Otp successfully generated");
         LOG.info("OTP==> "+String.valueOf(otp)+" for "+phoneNumber);
         return new ResponseEntity<>(apiResponse, HttpStatus.OK);

    }
    @PostMapping("/validateOtp")
    public ResponseEntity <?> validateOtp(@RequestBody OtpObject otpObject){

//Validate the Otp
        if(otpObject.getOtp() >= 0){
            int serverOtp = otpService.getOtp(otpObject.getPhoneNumber());
            if(serverOtp > 0){
                if(otpObject.getOtp() == serverOtp){

                    Customer customer = customerService.findByPhoneNumber(otpObject.getPhoneNumber());
                    if (customer!=null){
                        customer.setStatusDescription("numero de compte verifie");
                        customer.setVerified(true);
                        customer.setStatus("ACTIVE");
                        customerService.save(customer);
                        otpService.clearOTP(otpObject.getPhoneNumber());
                        apiResponse.setResponseCode("00");
                        apiResponse.setResponseMessage("SUCCESS");
                        apiResponse.setCustomer(customer);
                    }else{
                        apiResponse.setResponseCode("01");
                        apiResponse.setResponseMessage("USER NOT FOUND");
                    }
                }else{
                    apiResponse.setResponseCode("01");
                    apiResponse.setResponseMessage("WRONG OTP");
                }
            }else {
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage("WRONG OTP");
            }
        }else {
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("WRONG OTP");
        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
