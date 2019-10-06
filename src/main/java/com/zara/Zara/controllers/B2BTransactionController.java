package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Business;
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

import static com.zara.Zara.constants.Configs.PERCENTAGE_ON_B2B;
import static com.zara.Zara.constants.ConstantVariables.TRANSACITION_B2B;

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
    BigDecimal originalAmount, charges,finalAmount;
    @Autowired
    ICommissionSettingService commissionSettingService;
    @PostMapping("/post")
    public ResponseEntity<?> post(@RequestBody B2CRequest requestBody) throws UnsupportedEncodingException {
        Business business = businessService.findByBusinessNumber(requestBody.getSender());
        originalAmount = new BigDecimal(requestBody.getAmount());
        charges = commissionSettingService.getCommission(new BigDecimal(requestBody.getAmount()));
        finalAmount = originalAmount.subtract(charges);
        if (business.isVerified()){

            if (business.getStatus().equals("ACTIVE")){
                if (bCryptPasswordEncoder.matches(requestBody.getPin(), business.getPassword())){

                                Business receiver = businessService.findByBusinessNumber(requestBody.getReceiver());
                                PesapayTransaction transaction = new PesapayTransaction();
                                transaction.setCreatedOn(new Date());
                                transaction.setCreationDate(System.currentTimeMillis());
                                transaction.setOriginalAmount(originalAmount);
                                transaction.setCharges(charges);
                                transaction.setFinalAmount(finalAmount);
                                transaction.setCreatedByBusiness(business);
                                transaction.setReceivedByBusiness(receiver);
                                transaction.setSender(business.getBusinessName());
                                transaction.setReceiver(receiver.getBusinessName());
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

                                        else if(business.getBalance().compareTo(originalAmount.add(charges))<0){
                                            transaction.setStatus("01");
                                            transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                                            transaction.setDescription("Transaction echoue. SOLDE DU BUSINESS EST EPUISE ");
                                            LOGGER.info("TRANSACTION FAILED BUSINESS BALANCE INSUFFICIENT ");
                                            Sms sms = new Sms();
                                            sms.setTo(business.getPhoneNumber());
                                            sms.setMessage("Votre solde est insuffisant pour envoyer "+originalAmount+" USD et supporter les frais de retrait. vous avez actuellement "+business.getBalance().setScale(2, BigDecimal.ROUND_UP)+" USD." +
                                                    "Il vous faut au moins "+originalAmount.add(charges)+"USD pour effectuer cette transaction");
                                            SmsService.sendSms(sms);
                                            if (!insufficientBalanceMessageAlreadySent){
                                                sendBalanceExaustedOnlyOnce(business.getPhoneNumber(), business.getBusinessName());
                                            }
                                            transactionService.addTransaction(transaction);
                                        }else {
                                            transaction.setStatus("00");
                                            transaction.setDescription("transaction reussie");
                                            transactionService.addTransaction(transaction);
                                            receiver.setBalance(receiver.getBalance().add(originalAmount));
                                            business.setBalance(business.getBalance().subtract(originalAmount.add(charges)));
                                            Business updatedReceiver = businessService.save(receiver);
                                            Business updatedSender = businessService.save(business);
                                            LOGGER.info("TRANSACTION SUCCESSFUL "+receiver.getBusinessName());
                                            Sms sms = new Sms();
                                            sms.setTo(receiver.getPhoneNumber());
                                            String msg1 = "vous avez recu. "+originalAmount+" " +
                                                    "USD via PesaPay de la part de "+updatedSender.getBusinessName() +
                                                    " numero de transactinon "+transaction.getTransactionNumber()+"" +
                                                    " votre solde balance est de "+updatedReceiver.getBalance().setScale(2, BigDecimal.ROUND_UP)+"" +
                                                    " USD. type de transaction B2B";

                                            sms.setMessage(msg1);
                                            SmsService.sendSms(sms);




                                            Sms sms2 = new Sms();
                                            sms2.setTo(business.getPhoneNumber());
                                            String msg2 =business.getBusinessName()+" vous avez envoye "+originalAmount+" USD via PesaPay a "+receiver.getBusinessName()+"." +
                                                    "les frais de transaction ont ete de "+ charges+" USD. votre balance actuelle est "+ this.updatedBusiness.getBalance().setScale(2, BigDecimal.ROUND_UP)+" USD. type de transaction B2B ";

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
                                        apiResponse.setResponseMessage("Cet identifiant n'a pas de compte Business sur PesaPay");
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
