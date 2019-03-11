package com.zara.Zara.controllers;

import com.stripe.exception.*;
import com.stripe.model.Charge;
import com.zara.Zara.entities.Business;
import com.zara.Zara.entities.Notification;
import com.zara.Zara.entities.PesapayTransaction;
import com.zara.Zara.services.IBusinessService;
import com.zara.Zara.services.INotificationService;
import com.zara.Zara.services.banking.StripeService;
import com.zara.Zara.utils.BusinessNumbersGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.models.ChargeRequest;
import com.zara.Zara.models.Sms;
import com.zara.Zara.models.TransactionRequestBody;
import com.zara.Zara.services.ICustomerService;
import com.zara.Zara.services.ITransactionService;
import com.zara.Zara.services.utils.SmsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;

import static com.zara.Zara.constants.ConstantVariables.TRANSACTION_CREDIT_CARD_DEPOSIT;
import static com.zara.Zara.constants.ConstantVariables.TRANSACTION_PAYPAL_DEPOSIT;


@RestController
@RequestMapping("/creditCardsTransactions")
@CrossOrigin(origins = "*")
public class CreditCardTransactionController {

    ApiResponse apiResponse = new ApiResponse();
    Sms sms = new Sms();
    @Autowired
    ICustomerService customerService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    Logger LOGGER = LogManager.getLogger(CreditCardTransactionController.class);
    @Autowired
    StripeService stripeService;
    Charge charge;
    @Autowired
    IBusinessService businessService;
    @Autowired
    INotificationService notificationService;
    @PostMapping("/post")
    public ResponseEntity<?> post(@RequestBody TransactionRequestBody request) throws UnsupportedEncodingException, CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
        Customer senderCustomer = customerService.findByPhoneNumber(request.getReceiver());

        if (senderCustomer==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'existe pas");
            LOGGER.info("SENDER ACCOUNT NOT FOUND FOR "+request.getReceiver());
        }

        else{
             try {
                 ChargeRequest chargeRequest = new ChargeRequest();
                 chargeRequest.setAmount(Integer.valueOf(request.getAmount()));
                 chargeRequest.setCurrency(ChargeRequest.Currency.USD);
                 chargeRequest.setDescription(" Transaction request by PesaPay User "+senderCustomer.getFullName());
                 chargeRequest.setStripeToken(request.getStripeToken());

                 charge = stripeService.charge(chargeRequest);
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
                         sms2.setMessage(senderCustomer.getFullName()+ " Vous venez de recevoir "+request.getAmount()+"USD venant de la carte bancaire"+
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

             }catch (Exception e){
                 apiResponse.setResponseCode("01");
                 apiResponse.setResponseMessage(e.getLocalizedMessage());
                 LOGGER.info("STRIPE_FAILURE_MESSAGE "+e.getLocalizedMessage());
                 e.printStackTrace();
             }

    }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);

}




    @PostMapping("/business/cardTopesapay")
    public ResponseEntity<?> cardToPesaPay(@RequestBody TransactionRequestBody request) throws UnsupportedEncodingException, CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
        Business business = businessService.findByBusinessNumber(request.getReceiver());

        if (business==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'existe pas");
            LOGGER.info("SENDER ACCOUNT NOT FOUND FOR "+request.getReceiver());
        }

        else{
            try {
                ChargeRequest chargeRequest = new ChargeRequest();
                chargeRequest.setAmount(Integer.valueOf(request.getAmount()));
                chargeRequest.setCurrency(ChargeRequest.Currency.USD);
                chargeRequest.setDescription(" Transaction request by PesaPay User "+business.getBusinessName());
                chargeRequest.setStripeToken(request.getStripeToken());

                charge = stripeService.charge(chargeRequest);
                if (charge.getStatus().equals("succeeded")){
                    apiResponse.setResponseCode("00");
                    apiResponse.setResponseMessage("Transaction Reussie");

                    PesapayTransaction transaction = new PesapayTransaction();
                    transaction.setAmount(new BigDecimal(request.getAmount()));
                    transaction.setCreatedOn(new Date());
                    transaction.setStatus("00");
                    transaction.setDescription("Deposit(card) successful");
                    transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                    transaction.setReceivedByBusiness(business);
                    transaction.setTransactionType(TRANSACTION_CREDIT_CARD_DEPOSIT);

                    PesapayTransaction createdTransaction = transactionService.addTransaction(transaction);
                    if (createdTransaction==null){
                        apiResponse.setResponseCode("01");
                        apiResponse.setResponseMessage("ECHEC");
                        LOGGER.info("TRANSACTION FAILED TO PERSIST TO DATABASE");
                    }else {


                        business.setBalance(business.getBalance().add(new BigDecimal(request.getAmount())));
                        Business updatedBusiness = businessService.save(business);

                        Sms sms2 = new Sms();
                        sms2.setTo(business.getPhoneNumber());
                        Notification notification = new Notification();
                        String msg =business.getBusinessName()+ " Vous venez de recevoir "+request.getAmount()+"USD venant de la carte bancaire"+
                                " du numero se terminant par "+request.getSender()+" "+" via PesaPay. "+
                                " type de transaction DEPOT VIA CARTE BANCAIRE. votre solde actuel est "+updatedBusiness.getBalance()+" USD. numero de transaction "+transaction.getTransactionNumber();
                        notification.setBusiness(business);
                        notification.setMessage(msg);
                        sms2.setMessage(msg);
                        SmsService.sendSms(sms2);
                        notificationService.save(notification);
                        apiResponse.setResponseCode("00");
                        apiResponse.setResponseMessage("TRANSACTION REUSSIE");
                        LOGGER.info("DEPOSIT TRANSACTION SUCCESSFUL "+transaction.getTransactionNumber());
                    }

                }else{
                    apiResponse.setResponseCode("01");
                    apiResponse.setResponseMessage(charge.getFailureMessage());
                    LOGGER.info("STRIPE_FAILURE_MESSAGE "+charge.getFailureMessage());
                }

            }catch (Exception e){
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage(e.getLocalizedMessage());
                LOGGER.info("STRIPE_FAILURE_MESSAGE "+e.getLocalizedMessage());
                e.printStackTrace();
            }

        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);

    }


    @PostMapping("/business/paypalTopesapay")
    public ResponseEntity<?> postForBusiness(@RequestBody TransactionRequestBody request) throws UnsupportedEncodingException, CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
        Business business = businessService.findByBusinessNumber(request.getReceiver());

        if (business==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'existe pas");
            LOGGER.info("SENDER ACCOUNT NOT FOUND FOR "+request.getReceiver());
        }

        else{
            try {

                    PesapayTransaction transaction = new PesapayTransaction();
                    transaction.setAmount(new BigDecimal(request.getAmount()));
                    transaction.setCreatedOn(new Date());
                    transaction.setStatus("00");
                    transaction.setDescription("Deposit(PayPal) successful");
                    transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                    transaction.setReceivedByBusiness(business);
                    transaction.setTransactionType(TRANSACTION_PAYPAL_DEPOSIT);

                    PesapayTransaction createdTransaction = transactionService.addTransaction(transaction);
                    if (createdTransaction==null){
                        apiResponse.setResponseCode("01");
                        apiResponse.setResponseMessage("ECHEC");
                        LOGGER.info("TRANSACTION FAILED TO PERSIST TO DATABASE");
                    }else {

                        apiResponse.setResponseCode("00");
                        apiResponse.setResponseMessage("Transaction Reussie");


                        business.setBalance(business.getBalance().add(new BigDecimal(request.getAmount())));
                        Business updatedBusiness = businessService.save(business);
                        Sms sms2 = new Sms();
                        sms2.setTo(business.getPhoneNumber());
                        Notification notification = new Notification();
                        String msg =business.getBusinessName()+ " Vous venez de deposer dans votre compte PesaPay "+request.getAmount()+"USD venant"+
                                "  "+request.getSender()+" "+" via PayPal. "+
                                " type de transaction DEPOT VIA PAYPAL. votre solde actuel est "+updatedBusiness.getBalance()+" USD. numero de transaction "+transaction.getTransactionNumber();
                        notification.setBusiness(business);
                        notification.setMessage(msg);
                        sms2.setMessage(msg);
                        SmsService.sendSms(sms2);
                        notificationService.save(notification);
                        apiResponse.setResponseCode("00");
                        apiResponse.setResponseMessage("TRANSACTION REUSSIE");
                    }



            }catch (Exception e){
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage(e.getLocalizedMessage());
                LOGGER.info("STRIPE_FAILURE_MESSAGE "+e.getLocalizedMessage());
                e.printStackTrace();
            }

        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);

    }


}
