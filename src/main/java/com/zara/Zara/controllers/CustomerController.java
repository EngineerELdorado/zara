package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.services.ICustomerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.zara.Zara.constants.Responses.PHONE_NUMBER_ALREADY_TAKEN;
import static com.zara.Zara.constants.Responses.USER_REGISTRATION_SUCCESS;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    ICustomerService customerService;
    Logger LOG = LogManager.getLogger(CustomerController.class);
    ApiResponse apiResponse = new ApiResponse();
    @PostMapping("/post")
    public ResponseEntity<?> save(@RequestBody Customer customer){

        customer.setPin(bCryptPasswordEncoder.encode(customer.getPin()));
        if (!isPhoneTaken(customer.getPhoneNumber())){
            Customer savedCustomer = customerService.addUser(customer);
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
}
