package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Business;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.models.LoginObject;
import com.zara.Zara.models.OtpObject;
import com.zara.Zara.models.PaymentSetting;
import com.zara.Zara.models.Sms;
import com.zara.Zara.services.ICustomerService;
import com.zara.Zara.services.utils.OtpService;
import com.zara.Zara.services.utils.SmsService;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import static com.zara.Zara.constants.Responses.PHONE_NUMBER_ALREADY_TAKEN;
import static com.zara.Zara.constants.Responses.USER_REGISTRATION_SUCCESS;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = "*")
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
    public ResponseEntity<?> save(@RequestBody Customer customer) throws UnsupportedEncodingException {

        customer.setPin(bCryptPasswordEncoder.encode(customer.getPin()));
        customer.setCreationDate(new Date());
        customer.setVerified(true);
        customer.setStatus("ACTIVE");
        customer.setStatusDescription("the customer needs to verify his phone number");
        customer.setBalance(new BigDecimal("0"));
        if (!isPhoneTaken(customer.getPhoneNumber())){
            Customer savedCustomer = customerService.save(customer);
            if (savedCustomer!=null){
                apiResponse.setResponseCode("00");
                apiResponse.setResponseMessage(USER_REGISTRATION_SUCCESS);
                apiResponse.setCustomer(savedCustomer);
                LOG.info("REGISTRATION SUCCESSFUL");
                Sms sms = new Sms();
                sms.setTo(savedCustomer.getPhoneNumber());
                sms.setMessage("Cher "+customer.getFullName()+" Bievenu sur PesaPay. maintenant vous pouvez retirer deposer " +
                        "payer en ligne transferer ainsi effectuer tout genre de payment avec votre telephone.");
                SmsService.sendSms(sms);
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

    @PostMapping("/login")
    public ResponseEntity<?>login(@RequestBody LoginObject loginObject){
        Customer customer = customerService.findByPhoneNumber(loginObject.getPhoneNumber());
        if (customer==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("numero introuvable");
            LOG.info("LOGIN FAILED FOR==> "+loginObject.getPhoneNumber()+" PHONE NUMBER NOT FOUND");
        }else{
            if (bCryptPasswordEncoder.matches(loginObject.getPin(), customer.getPin())){
                apiResponse.setResponseCode("00");
                apiResponse.setResponseMessage("Bienvenu");
                apiResponse.setCustomer(customer);
                LOG.info("LOGIN SUCCESSFUL FOR==> "+loginObject.getPhoneNumber()+" "+customer.getFullName());
            }else {
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage("pin incorrect");
                LOG.info("LOGIN FAILED FOR==> "+loginObject.getPhoneNumber()+" PIN INCORRECT");
            }
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
    public ResponseEntity <?> validateOtp(@RequestBody Customer otpObject) throws UnsupportedEncodingException {

//Validate the Otp
        if(Integer.parseInt(otpObject.getOtp()) >= 0){
            int serverOtp = otpService.getOtp(otpObject.getPhoneNumber());
            if(serverOtp > 0){
                if(Integer.parseInt(otpObject.getOtp()) == serverOtp){
                        otpService.clearOTP(otpObject.getPhoneNumber());
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

    @GetMapping("/findByPhoneNumber/{phoneNumber}")
    public ResponseEntity<?>findCustomberByPhoneNumber(@PathVariable String phoneNumber) throws UnsupportedEncodingException {
        if (!phoneNumber.startsWith("+")){
            phoneNumber= "+"+phoneNumber;
        }
        Customer existingCustomer = customerService.findByPhoneNumber(phoneNumber);
        if (existingCustomer==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce numero n'a pas de compte PesaPay");
        }else {
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("Success");
            apiResponse.setCustomer(existingCustomer);
        }
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }


    @GetMapping("/findAll")
    public ResponseEntity<?>findAll(@RequestParam("page") int page, @RequestParam("size") int size){
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC,"id"));
        Pageable pageable = new PageRequest(page,size,sort);
        Collection<Customer>customers = customerService.findAll(pageable).getContent();
        if (customers.size()==0){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("aucun resultat");
        }else{

            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage(pageable.getPageSize()+" customers");
            apiResponse.setCustomers(customers);
        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    
}
