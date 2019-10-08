package com.zara.Zara.controllers;

import com.stripe.exception.*;
import com.stripe.model.Charge;
import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Business;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.entities.Notification;
import com.zara.Zara.entities.PesapayTransaction;
import com.zara.Zara.models.Sms;
import com.zara.Zara.models.TransactionRequestBody;
import com.zara.Zara.services.IBusinessService;
import com.zara.Zara.services.ICustomerService;
import com.zara.Zara.services.INotificationService;
import com.zara.Zara.services.ITransactionService;
import com.zara.Zara.services.banking.StripeService;
import com.zara.Zara.services.utils.SmsService;
import com.zara.Zara.utils.BusinessNumbersGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;

import static com.zara.Zara.constants.ConstantVariables.*;

@RestController
@RequestMapping("/mobile-money")
@CrossOrigin(origins = "*")
public class MobileMoneyController {

    ApiResponse apiResponse = new ApiResponse();
    Sms sms = new Sms();
    @Autowired
    ICustomerService customerService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    Logger LOGGER = LogManager.getLogger(BankingTransactionController.class);
    @Autowired
    StripeService stripeService;
    Charge charge;
    @Autowired
    IBusinessService businessService;

    @Autowired
    INotificationService notificationService;
    TransactionRequestBody request;
    Business business;
    Customer customer;
    String name;
    String phone;

    @PostMapping("/business/withdraw")
    public ResponseEntity<?> businessPesaPayToMobileMoney(@RequestBody TransactionRequestBody request,
                                                          @RequestParam String service) throws UnsupportedEncodingException, CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {

        business = businessService.findByBusinessNumber(request.getSender());
        name = business.getBusinessName();
        phone = business.getPhoneNumber();
        this.request=request;
        if (business==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'existe pas");
            LOGGER.info("RECEIVER ACCOUNT NOT FOUND FOR "+request.getSender());
        }
        else if (business.getBalance().compareTo(new BigDecimal(request.getAmount()))<0){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Solde insuffisant. vous avez "+business.getBalance()+" USD");
            LOGGER.info("INSUFFICIENT FUNDS"+request.getReceiver());
        }
        else if (!business.getStatus().equals("ACTIVE")){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'est pas actif. veillez contacter le service clientel de PesaPay");
            LOGGER.info("SENDER ACCOUNT NOT ACTIVE FOR "+request.getReceiver());
        }else if (!business.isVerified()){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'est pas encore verifie");
            LOGGER.info("SENDER ACCOUNT NOT VERIFIED FOR "+request.getReceiver());

        }
//        else if (!bCryptPasswordEncoder.matches(request.getPin(), customer.getPin())){
//            apiResponse.setResponseCode("01");
//            apiResponse.setResponseMessage("votre pin est incorrect");
//            LOGGER.info("WRONG PIN FOR "+request.getSender());
//        }

        else{
            try {

                PesapayTransaction transaction = new PesapayTransaction();
                transaction.setFinalAmount(new BigDecimal(request.getAmount()));
                transaction.setCreatedOn(new Date());
                transaction.setCreationDate(System.currentTimeMillis());
                transaction.setStatus("02");
                transaction.setForPaypalEmail(request.getForPaypalEmail());
                transaction.setDescription("transfer ver mobile money en suspens. en destination du compte "+service+" "+request.getReceiver());
                transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                transaction.setCreatedByBusiness(business);
                transaction.setSender(business.getBusinessName());
                transaction.setReceiver(service.toUpperCase()+" "+request.getReceiver());
                if (service.equals("mpesa")){
                    transaction.setTransactionType(TRANSACTION_MPESA_WITHRAWAL);
                }else if (service.equals("airtel_money")){
                    transaction.setTransactionType(TRANSACTION_AIRTEL_WITHRAWAL);
                }else if (service.equals("orange_money")){
                    transaction.setTransactionType(TRANSACTION_ORANGE_WITHRAWAL);
                }
                process(transaction, service);


            }catch (Exception e){
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage(e.getLocalizedMessage());
                LOGGER.info("STRIPE_FAILURE_MESSAGE "+e.getLocalizedMessage());
                e.printStackTrace();
            }

        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);

    }


    @PostMapping("/customer/withdraw")
    public ResponseEntity<?> customerPesaPayToMobileMoney(@RequestBody TransactionRequestBody request,
                                                     @RequestParam String service) throws UnsupportedEncodingException, CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {

        customer = customerService.findByPhoneNumber(request.getSender());
        name = customer.getFullName();
        phone = customer.getPhoneNumber();
        this.request=request;
        if (customer==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'existe pas");
            LOGGER.info("RECEIVER ACCOUNT NOT FOUND FOR "+request.getSender());
        }
        else if (customer.getBalance().compareTo(new BigDecimal(request.getAmount()))<0){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Solde insuffisant. vous avez "+customer.getBalance()+" USD");
            LOGGER.info("INSUFFICIENT FUNDS"+request.getReceiver());
        }
        else if (!customer.getStatus().equals("ACTIVE")){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'est pas actif. veillez contacter le service clientel de PesaPay");
            LOGGER.info("SENDER ACCOUNT NOT ACTIVE FOR "+request.getReceiver());
        }else if (!customer.isVerified()){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'est pas encore verifie");
            LOGGER.info("SENDER ACCOUNT NOT VERIFIED FOR "+request.getReceiver());

        }
//        else if (!bCryptPasswordEncoder.matches(request.getPin(), customer.getPin())){
//            apiResponse.setResponseCode("01");
//            apiResponse.setResponseMessage("votre pin est incorrect");
//            LOGGER.info("WRONG PIN FOR "+request.getSender());
//        }

        else{
            try {

                PesapayTransaction transaction = new PesapayTransaction();
                transaction.setOriginalAmount(new BigDecimal(request.getAmount()));
                transaction.setCharges(new BigDecimal(0));
                transaction.setFinalAmount(new BigDecimal(request.getAmount()));
                transaction.setCreatedOn(new Date());
                transaction.setCreationDate(System.currentTimeMillis());
                transaction.setStatus("02");
                transaction.setForPaypalEmail(request.getForPaypalEmail());
                transaction.setDescription("transfer ver mobile money en suspens. en destination du compte "+service+" "+request.getReceiver());
                transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                transaction.setCreatedByCustomer(customer);
                transaction.setSender(customer.getFullName());
                transaction.setReceiver(service.toUpperCase()+" "+request.getReceiver());
                if (service.equalsIgnoreCase("mpesa")){
                    transaction.setTransactionType(TRANSACTION_MPESA_WITHRAWAL);
                }else if (service.equalsIgnoreCase("airtel_money")){
                    transaction.setTransactionType(TRANSACTION_AIRTEL_WITHRAWAL);
                }else if (service.equalsIgnoreCase("orange_money")){
                    transaction.setTransactionType(TRANSACTION_ORANGE_WITHRAWAL);
                }
                process(transaction, service);


            }catch (Exception e){
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage(e.getLocalizedMessage());
                LOGGER.info("STRIPE_FAILURE_MESSAGE "+e.getLocalizedMessage());
                e.printStackTrace();
            }

        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);

    }


    public void process(PesapayTransaction transaction, String service) throws UnsupportedEncodingException {

        PesapayTransaction createdTransaction = transactionService.addTransaction(transaction);
        if (createdTransaction==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("ECHEC");
            LOGGER.info("TRANSACTION FAILED TO PERSIST TO DATABASE");
        }else {

            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("Transaction Reussie");
            Notification notification=null;
            Sms sms2= new Sms();
            String msg="";
            if (business!=null){
                business.setBalance(business.getBalance().subtract(new BigDecimal(request.getAmount())));
                Business updatedBusiness = businessService.save(business);

                sms2.setTo(phone);
                notification = new Notification();
               msg = " Votre transfer PesaPay A "+service+" est en cours. montant "+request.getAmount()+" USD. la somme sera disponiblea dans votre compte "+service+
                        "  "+
                        " dans moins de 3h. Votre solde actuel est de "+updatedBusiness.getBalance()+" USD. no de transaction "+createdTransaction.getTransactionNumber();
            }

            if (customer!=null){
                customer.setBalance(customer.getBalance().subtract(new BigDecimal(request.getAmount())));
                Customer updatedCustomer = customerService.save(customer);

                sms2.setTo(phone);
                notification = new Notification();
                msg = " Votre transfer PesaPay A "+service+" est en cours. montant "+request.getAmount()+" USD. la somme sera disponiblea dans votre compte "+service+
                        "  "+
                        " dans moins de 3h. Votre solde actuel est de "+updatedCustomer.getBalance().setScale(2,BigDecimal.ROUND_UP) +"USD. no de transaction "+createdTransaction.getTransactionNumber();
            }


            if (business!=null){
                notification.setBusiness(business);
            }
            if (customer!=null){
                notification.setCustomer(customer);
            }

            notification.setMessage(msg);
            notification.setDate(new Date());
            sms2.setMessage(msg);
            SmsService.sendSms(sms2);
            notificationService.save(notification);
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("TRANSACTION REUSSIE");
        }

    }

}
