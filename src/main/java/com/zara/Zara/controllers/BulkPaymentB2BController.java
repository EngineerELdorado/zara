package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.*;
import com.zara.Zara.models.BulkPaymentRequest;
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
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.zara.Zara.constants.Configs.*;
import static com.zara.Zara.constants.ConstantVariables.TRANSACTION_BULKPAYMENT;

@RestController
@RequestMapping("/bulkpayments/b2b")
@CrossOrigin(origins = "*")
public class BulkPaymentB2BController {

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
    INotificationService notificationService;
    ApiResponse apiResponse = new ApiResponse();
    boolean insufficientBalanceMessageAlreadySent=false;
    Logger LOGGER = LogManager.getLogger(BankTransferController.class);
    BigDecimal originalAmount,charges,finalAmount;
    @Autowired
    ICommissionSettingService commissionSettingService;
    @PostMapping("/post")
    public ResponseEntity<?>post(@RequestBody BulkPaymentRequest requestBody) throws UnsupportedEncodingException {
        Business business = businessService.findByBusinessNumber(requestBody.getSender());
        LOGGER.info("BUSINESS "+business.toString());
        Collection<BulkBeneficiary>bulkBeneficiaries = bulkBeneficiaryService.findByCaterory(Long.parseLong(requestBody.getCategoryId()));
        LOGGER.info("TOTAL OF BENEFICIARIES =>"+bulkBeneficiaries.size());
        int successCount =0;
        Business updatedBusiness = null;
        int failureCount = 0;
        BigDecimal amountSpent = new BigDecimal("0");
        BigDecimal totalCharges = new BigDecimal("0");
        if (business.isVerified()){

            if (business.getStatus().equals("ACTIVE")){
                if (bCryptPasswordEncoder.matches(requestBody.getPin(), business.getPassword())){

                    if (bulkBeneficiaries.size()>0){
                        for (BulkBeneficiary beneficiary: bulkBeneficiaries){

                            LOGGER.info("PROCESSING PAYMENT FOR "+beneficiary.getName());
                            ExecutorService executorService = Executors.newFixedThreadPool(8);
                            Business receiver = businessService.findByBusinessNumber(beneficiary.getBusinessNumber());
                            PesapayTransaction transaction = new PesapayTransaction();
                            transaction.setCreatedOn(new Date());
                            transaction.setCreationDate(System.currentTimeMillis());
                            originalAmount =beneficiary.getAmount();
                            charges = commissionSettingService.getCommission(originalAmount);
                            finalAmount = originalAmount.subtract(charges);
                            transaction.setOriginalAmount(originalAmount);
                            transaction.setCharges(charges);
                            transaction.setFinalAmount(finalAmount);
                            transaction.setCreatedByBusiness(business);
                            transaction.setReceivedByBusiness(receiver);
                            transaction.setSender(business.getBusinessName());
                            transaction.setReceiver(receiver.getBusinessName());
                            transaction.setTransactionType(TRANSACTION_BULKPAYMENT);
                            transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));

                            if (business.getStatus().equals("ACTIVE")){
                                LOGGER.info("ACCOUNT IS ACTIVE FOR "+business.getBusinessName());
                                if (receiver!=null){
                                    LOGGER.info("ACCOUNT EXISTS FOR "+receiver.getBusinessName());
                                    if (!receiver.isVerified()){
                                        transaction.setStatus("01");
                                        transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                                        transaction.setDescription("Transaction echoue. compte non verifie pour le numero "+beneficiary.getPhoneNumber());
                                        failureCount++;
                                        LOGGER.info("TRANSACTION FAILED ACCOUNT NOT VERIFIED "+receiver.getBusinessName());
                                        updatedBusiness=business;
                                        transactionService.addTransaction(transaction);
                                    }else if (!receiver.getStatus().equals("ACTIVE")){
                                        transaction.setStatus("01");
                                        transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                                        transaction.setDescription("Transaction echoue. compte NON ACTIF pour le numero "+beneficiary.getPhoneNumber());
                                        failureCount++;
                                        LOGGER.info("TRANSACTION FAILED ACCOUNT NOT ACTIVE "+receiver.getBusinessName());
                                        updatedBusiness=business;
                                        transactionService.addTransaction(transaction);
                                    }

                                    else if(business.getBalance().compareTo(beneficiary.getAmount().add(charges))<0){
                                        transaction.setStatus("01");
                                        transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                                        transaction.setDescription("Transaction echoue. SOLDE DU BUSINESS EST EPUISE ");
                                        LOGGER.info("TRANSACTION FAILED BUSINESS BALANCE INSUFFICIENT ");
                                        if (!insufficientBalanceMessageAlreadySent){
                                            sendBalanceExaustedOnlyOnce(business.getPhoneNumber(), business.getBusinessName());
                                        }
                                        failureCount++;
                                        updatedBusiness=business;
                                        transactionService.addTransaction(transaction);
                                    }else {
                                        transaction.setStatus("00");
                                        transaction.setDescription("bulk transaction reussie");
                                        transactionService.addTransaction(transaction);
                                        receiver.setBalance(receiver.getBalance().add(finalAmount));
                                        business.setBalance(business.getBalance().subtract(originalAmount));
                                        Business updatedCustomer = businessService.save(receiver);
                                        updatedBusiness = businessService.save(business);
                                        successCount++;
                                        amountSpent= amountSpent.add(beneficiary.getAmount());
                                        totalCharges= totalCharges.add(charges);
                                        LOGGER.info("TRANSACTION SUCCESSFUL "+receiver.getBusinessName());
                                        Sms sms = new Sms();
                                        sms.setTo(receiver.getPhoneNumber());
                                        String msg1 = "transaction confirmee. vous avez recu"+originalAmount+" USD via PesaPay de la part de "+business.getBusinessName()+" " +
                                                ". type de transaction B2B. numero de transactinon "+transaction.getTransactionNumber()+"" +
                                                " votre solde balance est de "+updatedCustomer.getBalance().setScale(2, BigDecimal.ROUND_UP)+" USD";

                                        sms.setMessage(msg1);
                                        Notification notification1 = new Notification();
                                        notification1.setBusiness(receiver);
                                        notification1.setDate(new Date());
                                        notification1.setMessage(msg1);
                                        notificationService.save(notification1);


                                        try {
                                            SmsService.sendSms(sms);
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }else {
                                    transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                                    transaction.setStatus("01");
                                    transaction.setDescription("Transaction echoue. compte introuvable pour le numero "+beneficiary.getPhoneNumber());
                                    updatedBusiness=business;
                                    LOGGER.info("TRANSACTION FAILED ACCOUNT NOT FOUND "+beneficiary.getName());
                                }
                            }
                        }
                        Sms sms = new Sms();
                        sms.setTo(business.getPhoneNumber());
                        String msg2 =business.getBusinessName()+" vous avez effectuE "+bulkBeneficiaries.size()+" payments via PesaPay dont "+successCount+" reussis et "+failureCount+" echoues. le total de vos transfer en bulk est de "+amountSpent+"USD. votre solde actuel est de "+updatedBusiness.getBalance()+" USD. type de transaction BULK PAYMENT ";
                        sms.setMessage(msg2);
                        SmsService.sendSms(sms);

                        Notification notification2 = new Notification();
                        notification2.setBusiness(business);
                        notification2.setDate(new Date());
                        notification2.setMessage(msg2);
                        notificationService.save(notification2);
                        apiResponse.setResponseCode("00");
                        apiResponse.setResponseMessage(business.getBusinessName()+" vous avez effectuE "+bulkBeneficiaries.size()+" payments via PesaPay dont "+successCount+" reussis et "+failureCount+" echoues. le total de vos transfer en bulk est de "+amountSpent+"USD. le total des frais de transfer est de"+totalCharges+"USD. votre solde actuel est de "+updatedBusiness.getBalance()+" USD. type de transaction BULK PAYMENT ");

                    }
                    else{
                        apiResponse.setResponseCode("01");
                        apiResponse.setResponseMessage("Aucun beneficiaire dans cette categorie");
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
