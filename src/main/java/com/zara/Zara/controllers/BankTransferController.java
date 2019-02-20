package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.models.SafepayDto;
import com.zara.Zara.models.TransactionRequestBody;
import com.zara.Zara.models.safepay.SafePayRequest;
import com.zara.Zara.models.safepay.SafepayResponse;
import com.zara.Zara.models.safepay.shared.Ach;
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
        Ach ach = new Ach();
        ach.setAccountHolderName(request.getAccountHolderName());
        ach.setAccountNumber(request.getAccountNumber());
        ach.setAccountType(request.getAccountType());
        ach.setPayMethod(request.getPayMethod());
        ach.setRoutingNumber(request.getRoutingNumber());
        safePayRequest.setAmount(Float.parseFloat(request.getAmount()));
        safePayRequest.setMerchantRefNum(PESAPAY_ACCOUNT_NUMBER);
        safePayRequest.setAch(ach);


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
