package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Agent;
import com.zara.Zara.entities.Business;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.models.ChangePasswordRequest;
import com.zara.Zara.models.LoginObject;
import com.zara.Zara.models.OtpObject;
import com.zara.Zara.models.Sms;
import com.zara.Zara.services.IAgentService;
import com.zara.Zara.services.IBusinessService;
import com.zara.Zara.services.utils.OtpService;
import com.zara.Zara.services.utils.SmsService;
import com.zara.Zara.utils.BusinessNumbersGenerator;
import com.zara.Zara.utils.GenerateRandomStuff;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

@RestController
@RequestMapping("/businesses")
@CrossOrigin(origins = "*")
public class BusinessController {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    IBusinessService businessService;
    @Autowired
    OtpService otpService;
    ApiResponse apiResponse = new ApiResponse();
    Sms sms = new Sms();
    Logger LOG = LogManager.getLogger(CustomerController.class);
    @PostMapping("/post")
    public ResponseEntity<?>createBusiness(@RequestBody Business business) throws UnsupportedEncodingException {

        LOG.info(business.toString());
        business.setBusinessNumber(BusinessNumbersGenerator.generateBusinessNumber(businessService));
        business.setPin(bCryptPasswordEncoder.encode(business.getPassword()));
        business.setPassword(bCryptPasswordEncoder.encode(business.getPassword()));
        business.setStatus("ACTIVE");
        business.setVerified(true);
        business.setCreatedOn(new Date());
        business.setBalance(new BigDecimal("0"));
        Business business1 = businessService.save(business);
        if (business1!=null){
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("Enregistrement Reussi");
            apiResponse.setBusiness(business1);
            sms.setTo(business.getPhoneNumber());
            sms.setMessage(business.getBusinessName()+" Bievenu sur PesaPay. vous avez maintenant un compte BUSINESS. votre numero business" +
                    " est "+business1.getBusinessNumber());
            SmsService.sendSms(sms);


        }else{
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Enregistrement Echoue");
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?>login(@RequestBody LoginObject loginObject){
        Business business = businessService.findByEmail(loginObject.getEmail());
        if (business==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("identifiant non reconnu");
            LOG.info("LOGIN FAILED FOR==> "+loginObject.getPhoneNumber()+" BUSINESS NUMBER NOT FOUND");
        }else{
            if (bCryptPasswordEncoder.matches(loginObject.getPassword(), business.getPassword())){
                apiResponse.setResponseCode("00");
                apiResponse.setResponseMessage("Bienvenu");
                apiResponse.setBusiness(business);
                LOG.info("LOGIN SUCCESSFUL FOR==> "+loginObject.getBusinessNumber()+" "+business.getBusinessName());
            }else {
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage("mot de passe incorrect");
                LOG.info("LOGIN FAILED FOR==> "+loginObject.getBusinessNumber()+" password INCORRECT");
            }
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    public boolean isPhoneTaken(String businessNumber){
        Business business = businessService.findByBusinessNumber(businessNumber);
        if (business!=null){
            return true;
        }else {
            return false;
        }
    }

    @PostMapping("/generateOtp")
    public ResponseEntity<?> generateOtp(@RequestBody OtpObject otpObject) throws UnsupportedEncodingException {
        int otp = otpService.generateOTP(otpObject.getPhoneNumber());
        apiResponse.setResponseCode("00");
        apiResponse.setResponseMessage("Otp successfully generated");
        Sms sms = new Sms();
        sms.setTo(otpObject.getPhoneNumber());
        sms.setMessage("votre code de verification pour PesaPay est "+String.valueOf(otp));
        SmsService.sendSms(sms);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);

    }
    @PostMapping("/validateOtp")
    public ResponseEntity <?> validateOtp(@RequestBody OtpObject otpObject) throws UnsupportedEncodingException {

//Validate the Otp
        if(otpObject.getOtp() >= 0){
            int serverOtp = otpService.getOtp(otpObject.getPhoneNumber());
            if(serverOtp > 0){
                if(otpObject.getOtp() == serverOtp){

                    Business business = businessService.findByPhoneNumber(otpObject.getPhoneNumber());
                    if (business!=null){
                        business.setStatusDescription("numero de compte verifie");
                        business.setVerified(true);
                        business.setStatus("ACTIVE");
                        businessService.save(business);
                        otpService.clearOTP(otpObject.getPhoneNumber());
                        apiResponse.setResponseCode("00");
                        apiResponse.setResponseMessage("SUCCESS");
                        apiResponse.setBusiness(business);
                        Sms sms = new Sms();
                        sms.setTo(otpObject.getPhoneNumber());
                        sms.setMessage("Cher "+business.getBusinessName()+" Bievenu sur PesaPay. vous etes maintenant business");
                        SmsService.sendSms(sms);
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


    @GetMapping("/findByBusinessNumber/{businessNumber}")
    public ResponseEntity<?>findAgentNumber(@PathVariable String businessNumber) throws UnsupportedEncodingException {
        Business business = businessService.findByBusinessNumber(businessNumber);
        if (business==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce numero n'a pas de compte Business sur PesaPay");
        }
        else {
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("Success");
            apiResponse.setBusiness(business);
        }
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    @PostMapping("/changePassword/{businessNumber}")
    public ResponseEntity<?>changePassword(@RequestBody ChangePasswordRequest changePasswordRequest,@PathVariable String businessNumber){
        Business business = businessService.findByBusinessNumber(businessNumber);
        if (business==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Business introuvable");
        }else{
            if (bCryptPasswordEncoder.matches(changePasswordRequest.getCurrentPassword(), business.getPassword())){
                business.setPassword(bCryptPasswordEncoder.encode(changePasswordRequest.getNewPassword()));
                apiResponse.setResponseCode("00");
                apiResponse.setResponseMessage("Operation Reussie");
                businessService.save(business);
            }else{
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage("Le mot de passe actuel est incorrect");
            }
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @PostMapping("/update/{businessNumber}")
    public ResponseEntity<?>update(@RequestBody Business business,@PathVariable String businessNumber){
        Business business1 = businessService.findByBusinessNumber(businessNumber);
        if (business1==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Business introuvable");
        }else{
            business1.setAddress(business.getAddress());
            business1.setPhoneNumber(business.getPhoneNumber());
            business1.setBusinessName(business.getBusinessName());
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("Business successfully updated");
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
