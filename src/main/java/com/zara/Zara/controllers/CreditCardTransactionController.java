package com.zara.Zara.controllers;

import com.stripe.exception.*;
import com.stripe.model.Charge;
import com.zara.Zara.entities.Agent;
import com.zara.Zara.entities.PesapayTransaction;
import com.zara.Zara.services.StripeService;
import com.zara.Zara.utils.BusinessNumbersGenerator;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import com.stripe.model.Charge;
import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.models.ChargeRequest;
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
import java.util.*;

import static com.zara.Zara.constants.ConstantVariables.TRANSACTION_CREDIT_CARD_DEPOSIT;
import static com.zara.Zara.constants.ConstantVariables.TRANSACTION_DEPOSIT;


@RestController
@RequestMapping("/creditCardsTransactions")
public class CreditCardTransactionController {

    ApiResponse apiResponse = new ApiResponse();
    Sms sms = new Sms();
    @Autowired
    ICustomerService customerService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    Logger LOGGER = LogManager.getLogger(CustomerTransferController.class);
    @Autowired
    StripeService stripeService;

    @PostMapping("/post")
    public ResponseEntity<?> post(@RequestBody TransactionRequestBody request) throws UnsupportedEncodingException, CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
        Customer senderCustomer = customerService.findByPhoneNumber(request.getReceiver());

        if (senderCustomer==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'existe pas");
            LOGGER.info("SENDER ACCOUNT NOT FOUND FOR "+request.getReceiver());
        }

        else{
               ChargeRequest chargeRequest = new ChargeRequest();
               chargeRequest.setAmount(Integer.valueOf(request.getAmount()));
               chargeRequest.setCurrency(ChargeRequest.Currency.USD);
               chargeRequest.setDescription(" Transaction request by PesaPay User "+senderCustomer.getFullName());
               chargeRequest.setStripeToken(request.getStripeToken());

              Charge charge = stripeService.charge(chargeRequest);
              if (charge.getStatus().equals("succeeded")){
                  apiResponse.setResponseCode("00");
                  apiResponse.setResponseMessage("Transaction Reussie");

                  PesapayTransaction transaction = new PesapayTransaction();
                  transaction.setAmount(new BigDecimal(request.getAmount()));
                  transaction.setCreatedOn(new Date());
                  transaction.setStatus("00");
                  transaction.setDescription("Deposit(card) successful");
                  transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                  transaction.setReceivedByCustomer(senderCustomer);
                  transaction.setTransactionType(TRANSACTION_CREDIT_CARD_DEPOSIT);

                  PesapayTransaction createdTransaction = transactionService.addTransaction(transaction);
                  if (createdTransaction==null){
                      apiResponse.setResponseCode("01");
                      apiResponse.setResponseMessage("ECHEC");
                      LOGGER.info("TRANSACTION FAILED TO PERSIST TO DATABASE");
                  }else {


                      senderCustomer.setBalance(senderCustomer.getBalance().add(new BigDecimal(request.getAmount())));
                      Customer updatedCustomer = customerService.save(senderCustomer);

                      Sms sms2 = new Sms();
                      sms2.setTo(senderCustomer.getPhoneNumber());
                      sms2.setMessage(senderCustomer.getFullName()+ " vous venez de recevoir "+request.getAmount()+"USD venant d'une carte bancaire"+
                              " du numero "+request.getSender()+" "+" via PesaPay. "+
                              " type de transaction DEPOT VIA CARTE BANCAIRE. votre solde actuel est "+updatedCustomer.getBalance()+" USD. numero de transaction "+transaction.getTransactionNumber());
                      SmsService.sendSms(sms2);

                      apiResponse.setResponseCode("00");
                      apiResponse.setResponseMessage("TRANSACTION REUSSIE");
                      LOGGER.info("DEPOSIT TRANSACTION SUCCESSFUL "+transaction.getTransactionNumber());
              }

        }else{
                  apiResponse.setResponseCode("01");
                  apiResponse.setResponseMessage(charge.getFailureMessage());
                  LOGGER.info("STRIPE_FAILURE_MESSAGE "+charge.getFailureMessage());
              }


    }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);

}
}
