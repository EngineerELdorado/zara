package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.BulkBeneficiary;
import com.zara.Zara.entities.Business;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.entities.PesapayTransaction;
import com.zara.Zara.models.Sms;
import com.zara.Zara.models.TransactionRequestBody;
import com.zara.Zara.services.*;
import com.zara.Zara.services.utils.SmsService;
import com.zara.Zara.utils.BusinessNumbersGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/bulkpayments")
public class BulkPaymentController {

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
    ApiResponse apiResponse = new ApiResponse();

    @PostMapping("/post}")
    public ResponseEntity<?>post(@RequestBody TransactionRequestBody requestBody){
        Business business = businessService.findByBusinessNumber(requestBody.getSender());
        Collection<BulkBeneficiary>bulkBeneficiaries = bulkBeneficiaryService.findByCaterory(Long.parseLong(requestBody.getBulkCategoryId()));
        int successCount =0;
        Business updatedBusiness = null;
        int failureCount = 0;
        if (business.isVerified()){

            if (business.getStatus().equals("ACTIVE")){
                if (bCryptPasswordEncoder.matches(requestBody.getPin(), business.getPin())){
                    for (BulkBeneficiary beneficiary: bulkBeneficiaries){
                        ExecutorService executorService = Executors.newFixedThreadPool(8);
                        Customer customer = customerService.findByPhoneNumber(beneficiary.getPhoneNumber());
                        PesapayTransaction transaction = new PesapayTransaction();
                        transaction.setCreatedOn(new Date());
                        transaction.setCreatedByBusiness(business);
                        if (business.getStatus().equals("ACTIVE")){
                            if (customer!=null){
                                if (customer.isVerified()){
                                    transaction.setStatus("01");
                                    transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                                    transaction.setDescription("Transaction echoue. compte non verifie pour le numero "+beneficiary.getPhoneNumber());
                                    failureCount++;
                                    transactionService.addTransaction(transaction);
                                }else if (!customer.getStatus().equals("ACTIVE")){
                                    transaction.setStatus("01");
                                    transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                                    transaction.setDescription("Transaction echoue. compte NON ACTIF pour le numero "+beneficiary.getPhoneNumber());
                                    failureCount++;
                                    transactionService.addTransaction(transaction);
                                }

                                else if(business.getBalance().compareTo(beneficiary.getAmount())<0){
                                    transaction.setStatus("01");
                                    transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                                    transaction.setDescription("Transaction echoue. SOLDE DU BUSINESS EST EPUISE ");
                                    failureCount++;
                                    transactionService.addTransaction(transaction);
                                }else {
                                    transaction.setStatus("00");
                                    transaction.setDescription("transaction reussie");
                                    transaction.setReceivedByCustomer(customer);
                                    transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                                    transactionService.addTransaction(transaction);
                                    customer.setBalance(customer.getBalance().add(beneficiary.getAmount()));
                                    business.setBalance(business.getBalance().subtract(beneficiary.getAmount()));
                                    Customer updatedCustomer = customerService.save(customer);
                                    updatedBusiness = businessService.save(business);
                                    Sms sms = new Sms();
                                    sms.setTo(customer.getPhoneNumber());
                                    sms.setMessage(customer.getFullName()+" vous avez recu "+beneficiary.getAmount()+" USD venant de "+business.getBusinessName()+" pour "+requestBody.getDescription()+" votre solde actuel est de "+updatedCustomer.getBalance()+" USD");
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

                            }
                        }
                    }
                    Sms sms = new Sms();
                    sms.setTo(business.getPhoneNumber());
                    sms.setMessage(business.getBusinessName()+" vous venez d'effectuer "+bulkBeneficiaries.size()+" payments dont "+successCount+" reussis et "+failureCount+" echoues. votre solde actuel est de "+updatedBusiness.getBalance()+" USD");
                }else{
                    apiResponse.setResponseCode("01");
                    apiResponse.setResponseMessage("Pin Incorrect");
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
}
