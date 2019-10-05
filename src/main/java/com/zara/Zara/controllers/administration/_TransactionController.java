package com.zara.Zara.controllers.administration;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Business;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.entities.PesapayTransaction;
import com.zara.Zara.models.Sms;
import com.zara.Zara.services.IBusinessService;
import com.zara.Zara.services.ICustomerService;
import com.zara.Zara.services.ITransactionService;
import com.zara.Zara.services.utils.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

@RestController
@RequestMapping("/admins/transactions")
@CrossOrigin(origins = "*")
public class _TransactionController {

    @Autowired
    ITransactionService transactionService;
    ApiResponse apiResponse = new ApiResponse();
    @Autowired
    ICustomerService customerService;
    @Autowired
    IBusinessService businessService;
    @GetMapping("/find-all")
    public ResponseEntity<?>findAll(@RequestParam int page, @RequestParam int size,
                                    @RequestParam Long start,@RequestParam Long end,
                                    @RequestParam(required = false) String param){
        Page<PesapayTransaction>transactions;


        if (param.length()>0){
            transactions = transactionService.filter(page,size,param.toLowerCase());
        }else{
            transactions = transactionService.findAll(page, size, start,end,param.toLowerCase());
        }
        apiResponse.setData(transactions);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/approve/{id}")
    public ResponseEntity<?> approveTransaction(Long id) throws UnsupportedEncodingException {

        PesapayTransaction transaction = transactionService.findOne(id);
        Customer customer = transaction.createdByCustomer;
        Business business = transaction.createdByBusiness;
        String service ="";
        if(transaction.getTransactionType().contains("AIRTEL")){
            service="Airtel money";
        }
        else if(transaction.getTransactionType().contains("ORGANGE")){
            service="Orange money";
        }else if(transaction.getTransactionType().contains("MPESA")){
            service="Mpesa";
        }else if(transaction.getTransactionType().contains("PAYPAL")){
            service="PayPal";
        }
        if(customer!=null){

            transaction.setStatus("00");
            transaction.setDescription("Transaction envoyee A "+service);
            transactionService.addTransaction(transaction);
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("TRANSACTION APPROUVEE");
            //customer.setBalance(customer.getBalance().subtract(transaction.getOriginalAmount()));
            //Customer updatedCust = customerService.save(customer);
            Sms sms = new Sms();
            sms.setTo(customer.getPhoneNumber());
            sms.setMessage(customer.getFullName()+" Votre transaction numero "+transaction.getTransactionNumber() +
                    " vers "+service+" vient d etre approuvee");
            SmsService.sendSms(sms);
        }

        if(business!=null){

            transaction.setStatus("00");
            transaction.setDescription("Transaction envoyee A "+service);
            transactionService.addTransaction(transaction);
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("TRANSACTION APPROUVEE");
            //business.setBalance(business.getBalance().subtract(transaction.getOriginalAmount()));
            //Business business1 = businessService.save(business);
            Sms sms = new Sms();
            sms.setTo(business.getPhoneNumber());
            sms.setMessage(business.getBusinessName()+" Votre transaction numero "+transaction.getTransactionNumber() +
                    " vers "+service+" vient d etre approuvee.");
            SmsService.sendSms(sms);
        }



        // TODO: 03/10/2019  perform some processing logic here or send to RabbitMQ

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @GetMapping("/reject/{id}")
    public ResponseEntity<?> rejectTransaction(Long id) throws UnsupportedEncodingException {

        PesapayTransaction transaction = transactionService.findOne(id);
        Customer customer = transaction.createdByCustomer;
        Business business = transaction.createdByBusiness;
        String service ="";
        if(transaction.getTransactionType().contains("AIRTEL")){
            service="Airtel money";
        }
        else if(transaction.getTransactionType().contains("ORGANGE")){
            service="Orange money";
        }else if(transaction.getTransactionType().contains("MPESA")){
            service="Mpesa";
        }else if(transaction.getTransactionType().contains("PAYPAL")){
            service="PayPal";
        }
        if(customer!=null){

            transaction.setStatus("03");
            transaction.setDescription("Transaction envoyee A "+service);
            transactionService.addTransaction(transaction);
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("TRANSACTION REJETEE");
            //customer.setBalance(customer.getBalance().subtract(transaction.getOriginalAmount()));
            //Customer updatedCust = customerService.save(customer);
            Sms sms = new Sms();
            sms.setTo(customer.getPhoneNumber());
            sms.setMessage(customer.getFullName()+" Votre transaction numero "+transaction.getTransactionNumber() +
                    " vers "+service+" vient d etre rejetee");
            SmsService.sendSms(sms);
        }

        if(business!=null){

            transaction.setStatus("03");
            transaction.setDescription("Transaction envoyee A "+service);
            transactionService.addTransaction(transaction);
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("TRANSACTION REJETEE");
            //business.setBalance(business.getBalance().subtract(transaction.getOriginalAmount()));
            //Business business1 = businessService.save(business);
            Sms sms = new Sms();
            sms.setTo(business.getPhoneNumber());
            sms.setMessage(business.getBusinessName()+" Votre transaction numero "+transaction.getTransactionNumber() +
                    " vers "+service+" vient d etre rejetee.");
            SmsService.sendSms(sms);
        }



        // TODO: 03/10/2019  perform some processing logic here or send to RabbitMQ

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
