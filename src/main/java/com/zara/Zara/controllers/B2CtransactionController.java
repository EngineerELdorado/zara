package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.*;
import com.zara.Zara.models.B2CRequest;
import com.zara.Zara.models.Sms;
import com.zara.Zara.services.*;
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

import static com.zara.Zara.constants.ConstantVariables.TRANSACITION_B2C;

@RestController
@RequestMapping("/b2cTransactions")
@CrossOrigin(origins = "*")
public class B2CtransactionController {

    @Autowired
    IBulkCategoryService bulkCategoryService;
    @Autowired
    IBulkBeneficiaryService bulkBeneficiaryService;
    @Autowired
    ICustomerService customerService;
    @Autowired
    IBusinessService businessService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    INotificationService notificationService;
    ApiResponse apiResponse = new ApiResponse();
    boolean insufficientBalanceMessageAlreadySent=false;
    Logger LOGGER = LogManager.getLogger(BankTransferController.class);
    Business updatedBusiness=null;
    @PostMapping("/post")
    public ResponseEntity<?> post(@RequestBody B2CRequest requestBody) throws UnsupportedEncodingException {
        Business business = businessService.findByBusinessNumber(requestBody.getSender());
        if (business.isVerified()){

            if (business.getStatus().equals("ACTIVE")){
                if (bCryptPasswordEncoder.matches(requestBody.getPin(), business.getPassword())){

                            Customer customer = customerService.findByPhoneNumber(requestBody.getReceiver());
                            PesapayTransaction transaction = new PesapayTransaction();
                            transaction.setCreatedOn(new Date());
                            transaction.setFinalAmount(new BigDecimal(requestBody.getAmount()));
                            transaction.setTransactionType("b2c");
                            transaction.setCreatedByBusiness(business);
                            transaction.setReceivedByCustomer(customer);
                            transaction.setTransactionType(TRANSACITION_B2C);
                            transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));

                            if (business.getStatus().equals("ACTIVE")){
                                LOGGER.info("ACCOUNT IS ACTIVE FOR "+business.getBusinessName());
                                if (customer!=null){
                                    LOGGER.info("ACCOUNT EXISTS FOR "+customer.getFullName());
                                    if (!customer.isVerified()){
                                        transaction.setStatus("01");
                                        transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                                        transaction.setDescription("Transaction echoue. compte non verifie pour le numero "+customer.getPhoneNumber());

                                        LOGGER.info("TRANSACTION FAILED ACCOUNT NOT VERIFIED "+customer.getFullName());

                                        transactionService.addTransaction(transaction);
                                    }else if (!customer.getStatus().equals("ACTIVE")){
                                        transaction.setStatus("01");
                                        transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                                        transaction.setDescription("Transaction echoue. compte NON ACTIF pour le numero "+customer.getPhoneNumber());

                                        LOGGER.info("TRANSACTION FAILED ACCOUNT NOT ACTIVE "+customer.getFullName());

                                        transactionService.addTransaction(transaction);
                                    }

                                    else if(business.getBalance().compareTo(new BigDecimal(requestBody.getAmount()))<0){
                                        transaction.setStatus("01");
                                        transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                                        transaction.setDescription("Transaction echoue. SOLDE DU BUSINESS EST EPUISE ");
                                        LOGGER.info("TRANSACTION FAILED BUSINESS BALANCE INSUFFICIENT ");
                                        if (!insufficientBalanceMessageAlreadySent){
                                            sendBalanceExaustedOnlyOnce(business.getPhoneNumber(), business.getBusinessName());
                                        }
                                        transactionService.addTransaction(transaction);
                                    }else {
                                        transaction.setStatus("00");
                                        transaction.setDescription("bulk transaction reussie");
                                        transactionService.addTransaction(transaction);
                                        customer.setBalance(customer.getBalance().add(new BigDecimal(requestBody.getAmount())));
                                        business.setBalance(business.getBalance().subtract(new BigDecimal(requestBody.getAmount())));
                                        Customer updatedCustomer = customerService.save(customer);
                                        updatedBusiness = businessService.save(business);
                                        LOGGER.info("TRANSACTION SUCCESSFUL "+customer.getFullName());
                                        Sms sms = new Sms();
                                        sms.setTo(customer.getPhoneNumber());
                                        String msg1 =customer.getFullName()+" vous avez recu "+requestBody.getAmount()+" USD venant de "+business.getBusinessName()+" votre solde actuel est de "+updatedCustomer.getBalance()+" USD. type de transaction B2C DIRECT";
                                        sms.setMessage(msg1);
                                        SmsService.sendSms(sms);

                                        Sms sms2 = new Sms();
                                        sms2.setTo(business.getPhoneNumber());
                                        String msg2=business.getBusinessName()+" vous avez envoye "+requestBody.getAmount()+" USD via PesaPay a "+customer.getFullName()+". votre balance actuelle est "+updatedBusiness.getBalance()+" USD. type de transaction B2C DIRECT ";
                                        sms2.setMessage(msg2);
                                        SmsService.sendSms(sms2);

                                        Notification notification1 = new Notification();
                                        notification1.setCustomer(customer);
                                        notification1.setDate(new Date());
                                        notification1.setMessage(msg1);
                                        notificationService.save(notification1);

                                        Notification notification2 = new Notification();
                                        notification2.setBusiness(business);
                                        notification2.setDate(new Date());
                                        notification2.setMessage(msg2);
                                        notificationService.save(notification2);

                                    }
                                }else {
                                    transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                                    transaction.setStatus("01");
                                    transaction.setDescription("Transaction echoue. compte introuvable pour le numero "+customer.getPhoneNumber());
                                    LOGGER.info("TRANSACTION FAILED ACCOUNT NOT FOUND "+customer.getFullName());
                                }
                            }


                        apiResponse.setResponseCode("00");
                        apiResponse.setResponseMessage(business.getBusinessName()+" vous avez envoye "+requestBody.getAmount()+" USD via PesaPay a "+customer.getFullName()+"." +
                                " votre balance actuelle est "+updatedBusiness.getBalance()+" USD. type de transaction B2C DIRECT" );


                }else{
                    apiResponse.setResponseCode("01");
                    apiResponse.setResponseMessage("Mot de passe incorrect");
                }
            }else{
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage("votre business n'est pas actif");
            }
        }else{
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("votre business n'est pas encore verifie");
        }



        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    public void sendBalanceExaustedOnlyOnce(String to, String name) throws UnsupportedEncodingException {
        Sms sms = new Sms();
        sms.setTo(to);
        sms.setMessage(name+ " Votre solde est epuise. les payments vont echouer");
        SmsService.sendSms(sms);
        insufficientBalanceMessageAlreadySent=true;
    }
}
