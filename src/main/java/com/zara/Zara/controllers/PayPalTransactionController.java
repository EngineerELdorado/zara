package com.zara.Zara.controllers;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.models.Sms;
import com.zara.Zara.models.TransactionRequestBody;
import com.zara.Zara.services.ICustomerService;
import com.zara.Zara.services.ITransactionService;
import com.zara.Zara.services.utils.SmsService;
import com.zara.Zara.utils.GenerateRandomStuff;
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

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/paypalTransactions")
public class PayPalTransactionController {

    ApiResponse apiResponse = new ApiResponse();
    Sms sms = new Sms();
    @Autowired
    ICustomerService customerService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    Logger LOGGER = LogManager.getLogger(CustomerTransferController.class);

    @PostMapping("/withdrawFromPaypal")
    public ResponseEntity<?> post(@RequestBody TransactionRequestBody request) throws UnsupportedEncodingException {
        Customer senderCustomer = customerService.findByPhoneNumber(request.getSender());

        if (senderCustomer==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'existe pas");
            LOGGER.info("SENDER ACCOUNT NOT FOUND FOR "+request.getReceiver());
        }

        else{

            Amount amount = new Amount();
            amount.setCurrency("USD");
            amount.setTotal(request.getAmount());
            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);
            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");

            Payment payment = new Payment();
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(transactions);

            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setCancelUrl("https://example.com/cancel");
            redirectUrls.setReturnUrl("https://example.com/return");
            payment.setRedirectUrls(redirectUrls);

            try {
                APIContext apiContext = new APIContext("ASe5tZS_UuGeNckjVSaLhfsXzFLATBqMyXwE5KAfuZJjOUR0StT9OjQSZlONP6aKIvKusuK_S_IC0PSY",
                        "");
                Payment createdPayment = payment.create(apiContext);
                // For debug purposes only: System.out.println(createdPayment.toString());
            } catch (PayPalRESTException e) {
                // Handle errors
            } catch (Exception ex) {
                // Handle errors
            }

        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
