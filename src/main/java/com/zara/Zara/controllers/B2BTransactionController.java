package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Business;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.entities.Notification;
import com.zara.Zara.entities.PesapayTransaction;
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

import static com.zara.Zara.constants.ConstantVariables.TRANSACITION_B2B;
import static com.zara.Zara.constants.ConstantVariables.TRANSACITION_B2C;

@RestController
@RequestMapping("/b2bTransactions")
@CrossOrigin(origins = "*")
public class B2BTransactionController {

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

                            Business receiver = businessService.findByBusinessNumber(requestBody.getReceiver());
                            PesapayTransaction transaction = new PesapayTransaction();
                            transaction.setCreatedOn(new Date());
                            transaction.setAmount(new BigDecimal(requestBody.getAmount()));
                            transaction.setTransactionType("b2b");
                            transaction.setCreatedByBusiness(business);
                            transaction.setReceivedByBusiness(receiver);
                            transaction.setTransactionType(TRANSACITION_B2B);
                            transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                    if(business.getBusinessNumber().equals(receiver.getBusinessNumber())){
                        transaction.setStatus("01");
                        transaction.setDescription("transaction impossible. l'initiateur et le beneficiaire sont les memes");
                        transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                        apiResponse.setResponseCode("01");
                        apiResponse.setResponseMessage("transaction impossible. l'initiateur et le beneficiaire sont les memes");
                        transactionService.addTransaction(transaction);

                    }else{

                            if (receiver!=null){
                                LOGGER.info("ACCOUNT EXISTS FOR "+receiver.getBusinessName());
                                if (!receiver.isVerified()){
                                    transaction.setStatus("01");
                                    transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                                    transaction.setDescription("Transaction echoue. compte non verifie pour le numero "+receiver.getPhoneNumber());

                                    LOGGER.info("TRANSACTION FAILED ACCOUNT NOT VERIFIED "+receiver.getBusinessName());

                                    transactionService.addTransaction(transaction);
                                }

                                else if (!receiver.getStatus().equals("ACTIVE")){
                                    transaction.setStatus("01");
                                    transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                                    transaction.setDescription("Transaction echoue. compte NON ACTIF pour le numero "+receiver.getPhoneNumber());

                                    LOGGER.info("TRANSACTION FAILED ACCOUNT NOT ACTIVE "+receiver.getBusinessName());

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
                                    receiver.setBalance(receiver.getBalance().add(new BigDecimal(requestBody.getAmount())));
                                    business.setBalance(business.getBalance().subtract(new BigDecimal(requestBody.getAmount())));
                                    Business updatedBusiness = businessService.save(receiver);
                                    this.updatedBusiness = businessService.save(business);
                                    LOGGER.info("TRANSACTION SUCCESSFUL "+receiver.getBusinessName());
                                    Sms sms = new Sms();
                                    sms.setTo(receiver.getPhoneNumber());
                                    String msg1 = receiver.getBusinessName()+" vous avez recu "+requestBody.getAmount()+" USD venant de "+business.getBusinessName()+" votre solde actuel est de "+updatedBusiness.getBalance()+" USD. type de transaction B2B DIRECT";
                                    sms.setMessage(msg1);
                                    SmsService.sendSms(sms);




                                    Sms sms2 = new Sms();
                                    sms2.setTo(business.getPhoneNumber());
                                    String msg2 =business.getBusinessName()+" vous avez envoye "+requestBody.getAmount()+" USD via Setting a "+receiver.getBusinessName()+". votre balance actuelle est "+ this.updatedBusiness.getBalance()+" USD. type de transaction B2B DIRECT ";
                                    sms2.setMessage(msg2);
                                    SmsService.sendSms(sms2);
                                    apiResponse.setResponseCode("00");
                                    apiResponse.setResponseMessage(msg2);

                                    Notification notification1 = new Notification();
                                    notification1.setBusiness(receiver);
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
                                transaction.setDescription("Transaction echoue. compte introuvable pour le numero "+receiver.getPhoneNumber());
                                LOGGER.info("TRANSACTION FAILED ACCOUNT NOT FOUND "+receiver.getBusinessName());
                                apiResponse.setResponseCode("01");
                                apiResponse.setResponseMessage("Cet identifiant n'a pas de compte Business sur Setting");
                            }



                    }

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
