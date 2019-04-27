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

import static com.zara.Zara.constants.Configs.PERCENTAGE_ON_B2C;
import static com.zara.Zara.constants.Configs.PERCENTAGE_ON_C2C;
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
    BigDecimal originalAmount, charges,finalAmount;
    @PostMapping("/post")
    public ResponseEntity<?> post(@RequestBody B2CRequest requestBody) throws UnsupportedEncodingException {
        Business business = businessService.findByBusinessNumber(requestBody.getSender());

        originalAmount = new BigDecimal(requestBody.getAmount());
        charges = originalAmount.multiply(new BigDecimal(PERCENTAGE_ON_B2C))
                .multiply(new BigDecimal("100"));
        finalAmount = originalAmount;

        if (business.isVerified()){

            if (business.getStatus().equals("ACTIVE")){
                if (bCryptPasswordEncoder.matches(requestBody.getPin(), business.getPassword())){




                        Customer customer = customerService.findByPhoneNumber(requestBody.getReceiver());
                        PesapayTransaction transaction = new PesapayTransaction();
                        transaction.setCreatedOn(new Date());
                        transaction.setOriginalAmount(originalAmount);
                        transaction.setCharges(charges);
                        transaction.setFinalAmount(finalAmount);
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
                                    transaction.setDescription("bulk transaction reussie");
                                    transactionService.addTransaction(transaction);
                                    customer.setBalance(customer.getBalance().add(finalAmount));
                                    business.setBalance(business.getBalance().subtract(originalAmount.add(charges)));
                                    Customer updatedCustomer = customerService.save(customer);
                                    updatedBusiness = businessService.save(business);
                                    LOGGER.info("TRANSACTION SUCCESSFUL "+customer.getFullName());
                                    Sms sms = new Sms();
                                    sms.setTo(customer.getPhoneNumber());
                                    String msg1 = "transaction confirmee. vous avez recu "+originalAmount+" USD via PesaPay venant de "+business.getBusinessName()+" " +
                                            ". type de transaction B2C. numero de transactinon "+transaction.getTransactionNumber()+"" +
                                            " votre solde balance est de "+updatedCustomer.getBalance().setScale(2, BigDecimal.ROUND_UP)+" USD";

                                    sms.setMessage(msg1);
                                    SmsService.sendSms(sms);

                                    Sms sms2 = new Sms();
                                    sms2.setTo(business.getPhoneNumber());
                                    String msg2=business.getBusinessName()+" vous avez envoye "+originalAmount+" USD via PesaPay a "+customer.getFullName()+". Les frais de transfer on ete de "+charges+"USD. votre balance actuelle est "+updatedBusiness.getBalance().setScale(2,BigDecimal.ROUND_UP)+" USD. type de transaction B2C ";
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
