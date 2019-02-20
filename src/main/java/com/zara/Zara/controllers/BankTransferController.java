package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.models.TransactionRequestBody;
import com.zara.Zara.models.safepay.SafePayRequest;
import com.zara.Zara.models.safepay.SafepayResponse;
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
    public ResponseEntity<?>post(@RequestBody TransactionRequestBody requestBody){

        
        return  new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
