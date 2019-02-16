package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.entities.Transaction;
import com.zara.Zara.models.Sms;
import com.zara.Zara.models.TransactionRequestBody;
import com.zara.Zara.services.ICustomerService;
import com.zara.Zara.services.ITransactionService;
import com.zara.Zara.services.utils.SmsService;
import com.zara.Zara.utils.GenerateRandomStuff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;

import static com.zara.Zara.constants.ConstantVariables.TRANSACTION_CUSTOMER_RANSFER;

@RestController
@RequestMapping("/customerTransfers")
public class CustomerTransferController {


    ApiResponse apiResponse = new ApiResponse();
    Sms sms = new Sms();
    @Autowired
    ICustomerService customerService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/post")
    public ResponseEntity<?>post(@RequestBody TransactionRequestBody request) throws UnsupportedEncodingException {
        Customer senderCustomer = customerService.findByPhoneNumber(request.getSender());
        Customer receiverCustomer = customerService.findByPhoneNumber(request.getReceiver());

        if (senderCustomer==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'existe pas");
        }
        else if (!senderCustomer.getStatus().equals("ACTIVE")){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'est pas actif. veillez contacter le service clientel de PesaPay");
        }else if (!senderCustomer.isVerified()){
            apiResponse.setResponseCode("02");
            apiResponse.setResponseMessage("Votre compte n'est pas encore verifie");

        }else if (senderCustomer.getBalance().compareTo(new BigDecimal(request.getAmount()))<0){
           apiResponse.setResponseCode("01");
           apiResponse.setResponseMessage("Vous n'avez pas assez d'argent pour effectuer ce transfert. votre solde est de "+senderCustomer.getBalance()+" USD");
        }else if (!bCryptPasswordEncoder.matches(request.getPin(), senderCustomer.getPin())){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("votre pin est incorrect");
        }
        else if (receiverCustomer==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce compte n'existe pas");

        }
        else if (!receiverCustomer.isVerified()){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Le compte du beneficiare n'est pas encore verifier. "+receiverCustomer.getStatusDescription());
            sms.setTo(receiverCustomer.getPhoneNumber());
            sms.setMessage(receiverCustomer.getFullName()+" "+senderCustomer.getFullName()+" essaye de vous transferer de l'argent mais votre compte n'est pas verifie." +
                    " veillez faire verifier votre compte");
            SmsService.sendSms(sms);
        }
        else if (!receiverCustomer.getStatus().equals("ACTIVE")){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Le compte du beneficiare n'est pas actif. Pour raison: "+receiverCustomer.getStatusDescription());
            sms.setTo(receiverCustomer.getPhoneNumber());
            sms.setMessage(receiverCustomer.getFullName()+" "+senderCustomer.getFullName()+" essaye de vous transferer de l'argent mais votre compte n'est pas actif." +
                    " veillez faire faire activer votre compte");
            SmsService.sendSms(sms);
        }
        else{

            Transaction transaction = new Transaction();
            transaction.setCreatedOn(new Date());
            transaction.setAmount(new BigDecimal(request.getAmount()));
            transaction.setStatus("00");
            transaction.setDescription("Transaction Reussie");
            transaction.setTransactionNumber(GenerateRandomStuff.getRandomString(5));
            transaction.setTransactionType(TRANSACTION_CUSTOMER_RANSFER);
            Transaction createdTransaction = transactionService.addTransaction(transaction);
            if (createdTransaction==null){
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage("Transaction echoue pour des raisons techniques");
            }else{
                apiResponse.setResponseCode("00");
                apiResponse.setResponseMessage("Transfert Reussi");
                Sms sms1 = new Sms();
                sms1.setTo(receiverCustomer.getPhoneNumber());
                sms1.setMessage("Vous avez recu "+request.getAmount()+" venant de "+senderCustomer.getFullName()+"" +
                        "Date: "+String.valueOf(new Date()));
                SmsService.sendSms(sms1);

                Sms sms2 = new Sms();
                sms2.setTo(receiverCustomer.getPhoneNumber());
                sms2.setMessage("Vous avez envoye "+request.getAmount()+" A "+receiverCustomer.getFullName()+"" +
                        "Date: "+String.valueOf(new Date()));
                SmsService.sendSms(sms2);
            }

        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
