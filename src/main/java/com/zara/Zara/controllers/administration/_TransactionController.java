package com.zara.Zara.controllers.administration;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.entities.PesapayTransaction;
import com.zara.Zara.models.Sms;
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
            customer.setBalance(customer.getBalance().subtract(transaction.getOriginalAmount()));
            Customer updatedCust = customerService.save(customer);
            Sms sms = new Sms();
            sms.setTo(customer.getPhoneNumber());
            sms.setMessage(customer.getFullName()+" Votre transaction numero "+transaction.getTransactionNumber() +
                    " vers "+service+" vient d etre approuvee. votre solde PesaPay reste de "+updatedCust.getBalance().setScale(2, BigDecimal.ROUND_UP)+" USD");
            SmsService.sendSms(sms);
        }

        transaction.setStatus("APPROVED");
        apiResponse.setResponseCode("00");
        apiResponse.setResponseMessage("TRANSACTION APPROUVEE");

        // TODO: 03/10/2019  perform some processing logic here or send to RabbitMQ

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
