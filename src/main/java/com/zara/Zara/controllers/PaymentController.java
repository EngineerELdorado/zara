package com.zara.Zara.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.*;
import com.zara.Zara.models.CallBackData;
import com.zara.Zara.models.Sms;
import com.zara.Zara.models.TransactionRequestBody;
import com.zara.Zara.services.*;
import com.zara.Zara.services.banking.BusinessCallbackService;
import com.zara.Zara.services.utils.OtpService;
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

import static com.zara.Zara.constants.ConstantVariables.TRANSACTION__BILL_PAYMENT;
import static com.zara.Zara.constants.ConstantVariables.TRANSACTION__ONLINE_PAYMENT;

@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    ApiResponse apiResponse = new ApiResponse();
    Sms sms = new Sms();
    @Autowired
    ICustomerService customerService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    IBusinessService businessService;
    @Autowired
    OtpService otpService;
    @Autowired
    INotificationService notificationService;
    Logger LOGGER = LogManager.getLogger(CustomerTransferController.class);

    @PostMapping("/buy/post")
    public ResponseEntity<?> post(@RequestBody TransactionRequestBody requestBody) throws UnsupportedEncodingException, JsonProcessingException {


                    Business business = businessService.findByBusinessNumber(requestBody.getReceiver());
                     if (business==null){
                        apiResponse.setResponseCode("01");
                        apiResponse.setResponseMessage("CE BENEFICIAIRE N'A PAS ETE TROUVE");
                    }
                    else{
                        Customer customer = customerService.findByPhoneNumber(requestBody.getSender());
                        if (customer==null){
                            apiResponse.setResponseCode("01");
                            apiResponse.setResponseMessage("CE COMPTE CLIENT N'A PAS ETE TROUVE");
                        }
                        else if (!customer.getStatus().equals("ACTIVE")){
                            apiResponse.setResponseCode("01");
                            apiResponse.setResponseMessage("Votre compte n'est pas actif. veillez contacter le service clientel de PesaPay");
                            LOGGER.info("BUSINESS ACCOUNT NOT ACTIVE FOR "+requestBody.getSender());
                        }
                        else if (!customer.isVerified()){
                            apiResponse.setResponseCode("01");
                            apiResponse.setResponseMessage("Votre compte n'est pas encore verifie");
                            LOGGER.info("BUSINESS ACCOUNT NOT VERIFIED FOR "+requestBody.getSender());

                        }else if (!business.getStatus().equals("ACTIVE")){
                            apiResponse.setResponseCode("01");
                            apiResponse.setResponseMessage("ce BUSINESS n'est pas actif "+business.getBusinessName()+" "+business.getStatusDescription());
                            LOGGER.info("BUSINESS ACCOUNT NOT ACTIVE FOR "+requestBody.getSender());
                        }
                        else if (!customer.isVerified()){
                            apiResponse.setResponseCode("01");
                            apiResponse.setResponseMessage("ce BUSINESS n'est pas encore verifie "+business.getBusinessName()+" "+business.getStatusDescription());
                            LOGGER.info("BUSINESS ACCOUNT NOT VERIFIED FOR "+requestBody.getSender());

                        }else if (customer.getBalance().compareTo(new BigDecimal(requestBody.getAmount()))<0){
                            apiResponse.setResponseCode("01");
                            apiResponse.setResponseMessage("Solde insuffisant "+customer.getBalance()+" USD");
                            LOGGER.info("INSUFFICIENT BALANCE FOR "+requestBody.getSender());

                        }else if (!bCryptPasswordEncoder.matches(requestBody.getPin(), customer.getPin())){
                            apiResponse.setResponseCode("01");
                            apiResponse.setResponseMessage("Pin Incorrect");
                            LOGGER.info("INCORRECT PIN FOR "+requestBody.getSender());
                        }

                        else {
                            PesapayTransaction transaction = new PesapayTransaction();
                            transaction.setAmount(new BigDecimal(requestBody.getAmount()));
                            transaction.setCreatedOn(new Date());
                            transaction.setStatus("00");
                            if (requestBody.getDescription().equals("")){
                                transaction.setDescription("Payment de Facture");
                            }else{
                                transaction.setDescription(requestBody.getDescription());
                            }
                            if (!requestBody.getUniqueIdentifier().equals("")){
                                transaction.setUniqueIdentifier(requestBody.getUniqueIdentifier());

                                if (!business.getCallBackUrl().equals("") && business.getCallBackUrl()!=null){
                                    CallBackData callBackData = new CallBackData();
                                    callBackData.setAccountNumber(requestBody.getUniqueIdentifier());
                                    callBackData.setAmount(requestBody.getAmount());
                                    new BusinessCallbackService()
                                            .postData(callBackData,business.getCallBackUrl());
                                }

                            }

                            transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                            transaction.setCreatedByCustomer(customer);
                            transaction.setReceivedByBusiness(business);

                            transaction.setTransactionType(TRANSACTION__BILL_PAYMENT);

                            PesapayTransaction createdTransaction = transactionService.addTransaction(transaction);
                            if (createdTransaction==null){
                                apiResponse.setResponseCode("01");
                                apiResponse.setResponseMessage("ECHEC");
                                LOGGER.info("TRANSACTION FAILED TO PERSIST TO DATABASE");
                            }else {

                                customer.setBalance(customer.getBalance().subtract(new BigDecimal(requestBody.getAmount())));
                                Customer updatedCustomer = customerService.save(customer);
                                Sms sms1 = new Sms();
                                String msg1=customer.getFullName()+ " vous venez de payer "+requestBody.getAmount()+" USD A "+business.getBusinessName()+" via PesaPay. Description "+requestBody.getDescription()+
                                        ". type de transaction PAYMENT DE FACTURE. votre solde actuel est "+updatedCustomer.getBalance().setScale(2, BigDecimal.ROUND_UP)+" USD. numero de transaction "+transaction.getTransactionNumber();
                                sms1.setTo(customer.getPhoneNumber());
                                sms1.setMessage(msg1);
                                SmsService.sendSms(sms1);

                                Notification notification1 = new Notification();
                                notification1.setCustomer(customer);
                                notification1.setDate(new Date());
                                notification1.setMessage(msg1);
                                notificationService.save(notification1);

                                business.setBalance(business.getBalance().add(new BigDecimal(requestBody.getAmount())));
                                Business updatedBusiness = businessService.save(business);

                                Sms sms2 = new Sms();
                                sms2.setTo(business.getPhoneNumber());
                                String msg2 =business.getBusinessName()+ " vous venez de recevoir un payment de "+requestBody.getAmount()+" USD venant  "+customer.getFullName()+" via PesaPay. "+
                                        " type de transaction PAYMENT DE FACTURE. votre solde actuel est "+updatedBusiness.getBalance().setScale(2,BigDecimal.ROUND_UP)+" USD. numero de transaction "+transaction.getTransactionNumber();
                                sms2.setMessage(msg2);
                                SmsService.sendSms(sms2);

                                Notification notification2 = new Notification();
                                notification2.setBusiness(business);
                                notification2.setDate(new Date());
                                notification2.setMessage(msg1);
                                notificationService.save(notification2);

                                apiResponse.setResponseCode("00");
                                apiResponse.setResponseMessage("TRANSACTION REUSSIE");
                                LOGGER.info("DEPOSIT TRANSACTION SUCCESSFUL "+transaction.getTransactionNumber());
                                otpService.clearOTP(requestBody.getSender());
                            }

                        }
                    }



        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }



}
