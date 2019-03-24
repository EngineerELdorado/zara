package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Business;
import com.zara.Zara.models.*;
import com.zara.Zara.services.IBusinessService;
import com.zara.Zara.services.utils.OtpService;
import com.zara.Zara.services.utils.SmsService;
import com.zara.Zara.utils.EmailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;

import static com.zara.Zara.constants.Responses.PHONE_NUMBER_ALREADY_TAKEN;
import static com.zara.Zara.constants.Responses.USER_REGISTRATION_SUCCESS;

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
    @Autowired
    EmailService emailService;
    Logger LOG = LogManager.getLogger(CustomerController.class);
    @PostMapping("/post")
    public ResponseEntity<?>createBusiness(@RequestBody Business business) throws UnsupportedEncodingException {

        business.setPin(bCryptPasswordEncoder.encode(business.getPin()));
        business.setCreatedOn(new Date());
        business.setVerified(true);
        business.setStatus("ACTIVE");
        business.setStatusDescription("the customer verified");
        business.setBalance(new BigDecimal("0"));
        if (!isEmailTaken(business.getEmail())){
            Business savedBusiness = businessService.save(business);
            if (savedBusiness!=null){
                apiResponse.setResponseCode("00");
                apiResponse.setResponseMessage(USER_REGISTRATION_SUCCESS);
                apiResponse.setBusiness(savedBusiness);
                LOG.info("REGISTRATION SUCCESSFUL");
                Sms sms = new Sms();
                sms.setTo(savedBusiness.getPhoneNumber());
                sms.setMessage("Cher "+business.getBusinessName()+" Bievenu sur PesaPay. votre identifiant unique est: "+savedBusiness.getBusinessNumber());
                SmsService.sendSms(sms);
            }else{
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage(USER_REGISTRATION_SUCCESS);
                LOG.info("REGISTRATION FAILED. BAD REQUEST PROBABLY");
            }
        }else{
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage(PHONE_NUMBER_ALREADY_TAKEN);
            LOG.info("REGISTRATION FAILED. EMAIL ALREADY TAKEN");
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

    public boolean isEmailTaken(String businessNumber){
        Business business = businessService.findByEmail(businessNumber);
        if (business!=null){
            return true;
        }else {
            return false;
        }
    }

    @PostMapping("/generateOtp")
    public ResponseEntity<?> generateOtp(@RequestBody OtpObject otpObject) throws IOException, MessagingException {

       if (!isEmailTaken(otpObject.getEmail())){
           int otp = otpService.generateOTP(otpObject.getEmail());
           apiResponse.setResponseCode("00");
           apiResponse.setResponseMessage("Otp successfully generated");

           emailService.sendmail("votre code de verification pour PesaPay est "+ otp, otpObject.getEmail());
       }else {
           apiResponse.setResponseCode("01");
           apiResponse.setResponseMessage("Cet email a deja un compte");

       }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);

    }
    @PostMapping("/validateOtp")
    public ResponseEntity <?> validateOtp(@RequestBody Business otpObject) throws UnsupportedEncodingException {

//Validate the Otp
        if(Integer.parseInt(otpObject.getOtp()) >= 0){
            int serverOtp = otpService.getOtp(otpObject.getEmail());
            if(serverOtp > 0){
                if(Integer.parseInt(otpObject.getOtp()) == serverOtp){
                    otpService.clearOTP(otpObject.getEmail());
                    apiResponse.setResponseCode("00");
                    apiResponse.setResponseMessage("SUCCESS");

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
            businessService.save(business1);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/updateSettings/{businessNumber}")
    public ResponseEntity<?>updateSettings(@RequestBody PaymentSetting setting, @PathVariable String businessNumber){
        Business business1 = businessService.findByBusinessNumber(businessNumber);
        if (business1==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Business introuvable");
        }
        else if(!bCryptPasswordEncoder.matches(setting.getPin(), business1.getPassword())){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Pin Incorrect");
        }
        else{
            business1.setAirtelMoneyNumber(setting.getAirtelMoneyNumber());
            business1.setOrangeMoneyNumber(setting.getOrangeMoneyNumber());
            business1.setMpesaNumber(setting.getMpesaNmumber());
            business1.setPaypalemail(setting.getPaypalEmail());
            business1.setBankAccountNumber(setting.getBankAccountNumber());
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("Settings successfully updated");
            businessService.save(business1);
        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
