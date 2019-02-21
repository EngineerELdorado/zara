package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.models.SafepayDto;
import com.zara.Zara.models.TransactionRequestBody;
import com.zara.Zara.models.safepay.SafePayRequest;
import com.zara.Zara.models.safepay.SafepayResponse;
import com.zara.Zara.models.safepay.shared.Ach;
import com.zara.Zara.models.safepay.shared.BillingDetails;
import com.zara.Zara.models.safepay.shared.Profile;
import com.zara.Zara.services.banking.SafePayService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.zara.Zara.constants.ConstantVariables.PESAPAY_ACCOUNT_NUMBER;

@RestController
@RequestMapping("/bankTransfers")
public class BankTransferController {

    @Autowired
    SafePayService safePayService;
    Logger LOGGER = LogManager.getLogger(BankTransferController.class);
    ApiResponse apiResponse = new ApiResponse();
    @PostMapping("/post")
    public ResponseEntity<?>post(@RequestBody SafepayDto request){

        SafePayRequest safePayRequest = new SafePayRequest();
        safePayRequest.setAmount(Float.parseFloat(request.getAmount()));
        safePayRequest.setMerchantRefNum(PESAPAY_ACCOUNT_NUMBER);
        safePayRequest.setCustomerIp(request.getCustomerIp());
        Ach ach = new Ach();
        ach.setAccountHolderName(request.getAccountHolderName());
        ach.setAccountNumber(request.getAccountNumber());
        ach.setAccountType(request.getAccountType());
        ach.setPayMethod(request.getPayMethod());
        ach.setRoutingNumber(request.getRoutingNumber());

        Profile profile = new Profile();
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setEmail(request.getEmail());

        BillingDetails billingDetails = new BillingDetails();
        billingDetails.setCity(request.getCity());
        billingDetails.setCountry(request.getCountry());
        billingDetails.setPhone(request.getPhone());
        billingDetails.setState(request.getState());
        billingDetails.setStreet(request.getStreet());
        billingDetails.setZip(request.getZip());

        safePayRequest.setAch(ach);
        safePayRequest.setProfile(profile);
        safePayRequest.setBillingDetails(billingDetails);


        ResponseEntity responseEntity = safePayService.directDedit(safePayRequest);
        LOGGER.info("SAFEPAY_RESPONSE "+ responseEntity);
        if (responseEntity.getStatusCodeValue()==200 || responseEntity.getStatusCodeValue()==201){
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("TRANSACTION COMPLETED");
        }else{
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("TRANSACTION FAILED "+responseEntity.getStatusCodeValue());
        }
        return  new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
