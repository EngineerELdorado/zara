package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.models.Sms;
import com.zara.Zara.models.TransactionRequestBody;
import com.zara.Zara.services.IAgentService;
import com.zara.Zara.services.ICustomerService;
import com.zara.Zara.services.ITransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/onlinepayments")
public class OnlinePaymentController{

    ApiResponse apiResponse = new ApiResponse();
    Sms sms = new Sms();
    @Autowired
    ICustomerService customerService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    IAgentService agentService;
    Logger LOGGER = LogManager.getLogger(CustomerTransferController.class);

    public ResponseEntity<?>post(@RequestBody TransactionRequestBody transactionRequestBody){

        return null;

    }
}