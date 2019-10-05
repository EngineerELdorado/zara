package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.entities.PesapayTransaction;
import com.zara.Zara.models.Sms;
import com.zara.Zara.models.TransactionRequestBody;
import com.zara.Zara.services.ICustomerService;
import com.zara.Zara.services.ITransactionService;
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

import static com.zara.Zara.constants.Configs.PERCENTAGE_ON_B2B_BULK;
import static com.zara.Zara.constants.Configs.PERCENTAGE_ON_C2C;
import static com.zara.Zara.constants.ConstantVariables.TRANSACTION_CUSTOMER_RANSFER;

@RestController
@RequestMapping("/customerTransfers")
@CrossOrigin(origins = "*")
public class CustomerTransferController {


    ApiResponse apiResponse = new ApiResponse();
    Sms sms = new Sms();
    @Autowired
    ICustomerService customerService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    Logger LOGGER = LogManager.getLogger(CustomerTransferController.class);
    BigDecimal originalAmount,charges,finalAmount;
    @PostMapping("/post")
    public ResponseEntity<?>post(@RequestBody TransactionRequestBody request) throws UnsupportedEncodingException {
        Customer senderCustomer = customerService.findByPhoneNumber(request.getSender());
        Customer receiverCustomer = customerService.findByPhoneNumber(request.getReceiver());
        originalAmount =new BigDecimal(request.getAmount());
        charges = originalAmount.multiply(new BigDecimal(PERCENTAGE_ON_C2C))
                .divide(new BigDecimal("100"));
        finalAmount = originalAmount;
        if (senderCustomer==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'existe pas");
            LOGGER.info("SENDER ACCOUNT NOT FOUND FOR "+request.getReceiver());
        }
        else if (senderCustomer.getPhoneNumber().equals(receiverCustomer.getPhoneNumber())){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Transaction impossible. meme numeros de telephone");
        }
        else if (!senderCustomer.getStatus().equals("ACTIVE")){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'est pas actif. veillez contacter le service clientel de PesaPay");
            LOGGER.info("SENDER ACCOUNT NOT ACTIVE FOR "+request.getReceiver());
        }else if (!senderCustomer.isVerified()){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'est pas encore verifie");
            LOGGER.info("SENDER ACCOUNT NOT VERIFIED FOR "+request.getReceiver());

        }else if (!bCryptPasswordEncoder.matches(request.getPin(), senderCustomer.getPin())){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("votre pin est incorrect");
            LOGGER.info("WRONG PIN FOR "+request.getSender());
        }
        else if (senderCustomer.getBalance().compareTo(originalAmount.add(charges))<0){
           apiResponse.setResponseCode("01");
           apiResponse.setResponseMessage("Solde insuffisant");
            Sms sms = new Sms();
            sms.setTo(senderCustomer.getPhoneNumber());
            sms.setMessage("Votre solde est insuffisant pour envoyer "+originalAmount+" USD et supporter les frais de retrait. vous avez actuellement "+senderCustomer.getBalance().setScale(2, BigDecimal.ROUND_UP)+" USD." +
                    "Il vous faut au moins "+originalAmount.add(charges)+"USD pour effectuer cette transaction");
            SmsService.sendSms(sms);
            LOGGER.info("SENDER BALANCE INSUFFICIENT "+request.getSender());
        }
        else if (receiverCustomer==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Ce compte n'existe pas");
            LOGGER.info("RECEIVER ACCOUNT NOT FOUND FOR "+request.getReceiver());

        }
        else if (!receiverCustomer.isVerified()){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Le compte du beneficiare n'est pas encore verifier. "+receiverCustomer.getStatusDescription());
            sms.setTo(receiverCustomer.getPhoneNumber());
            sms.setMessage(receiverCustomer.getFullName()+" "+senderCustomer.getFullName()+" essaye de vous transferer de l'argent mais votre compte n'est pas verifie." +
                    " veillez faire verifier votre compte");
            SmsService.sendSms(sms);
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'est pas actif. veillez contacter le service clientel de PesaPay");

            LOGGER.info("RECEIVER ACCOUNT NOT VERIFIED FOR "+request.getReceiver());
        }
        else if (!receiverCustomer.getStatus().equals("ACTIVE")){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Le compte du beneficiare n'est pas actif. Pour raison: "+receiverCustomer.getStatusDescription());
            sms.setTo(receiverCustomer.getPhoneNumber());
            sms.setMessage(receiverCustomer.getFullName()+" "+senderCustomer.getFullName()+" essaye de vous transferer de l'argent mais votre compte n'est pas actif." +
                    " veillez faire faire activer votre compte");
            SmsService.sendSms(sms);
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'est pas actif. veillez contacter le service clientel de PesaPay");
            LOGGER.info("RECEIVER ACCOUNT NOT ACTIVE FOR "+request.getReceiver());
        }
        else{

            PesapayTransaction transaction = new PesapayTransaction();
            transaction.setCreatedOn(new Date());
            transaction.setCreationDate(System.currentTimeMillis());
            transaction.setOriginalAmount(originalAmount);
            transaction.setCharges(charges);
            transaction.setFinalAmount(finalAmount);
            transaction.setStatus("00");
            transaction.setDescription("Transaction Reussie");
            transaction.setCreatedByCustomer(senderCustomer);
            transaction.setReceivedByCustomer(receiverCustomer);
            transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
            transaction.setSender(senderCustomer.getFullName());
            transaction.setReceiver(receiverCustomer.getFullName());
            transaction.setTransactionType(TRANSACTION_CUSTOMER_RANSFER);

            PesapayTransaction createdTransaction = transactionService.addTransaction(transaction);
            senderCustomer.setBalance(senderCustomer.getBalance().subtract(originalAmount.add(charges)));
            receiverCustomer.setBalance(receiverCustomer.getBalance().add(originalAmount));
            if (createdTransaction==null){
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage("Transaction echoue pour des raisons techniques");
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage("Votre compte n'est pas actif. veillez contacter le service clientel de PesaPay");
                LOGGER.info("TRANSDACTION FAILED FOR UNKNOWN REASON. PLEASE CHECK THE DATABASE CONNECTION");
            }else{
                customerService.save(senderCustomer);
                Sms sms1 = new Sms();
                sms1.setTo(senderCustomer.getPhoneNumber());
                sms1.setMessage("Vous avez envoye "+originalAmount+" USD via PesaPay A " +
                        ""+receiverCustomer.getFullName()+". les frais de transaction ont ete de "+charges+"USD. type de transaction TRANSFER DIRECT. " +
                        "votre solde actuel est de "+senderCustomer.getBalance().setScale(2,BigDecimal.ROUND_UP)+
                        " USD. numero de transaction "+transaction.getTransactionNumber());
                SmsService.sendSms(sms1);
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage("Votre compte n'est pas actif. veillez contacter le service clientel de PesaPay");
                LOGGER.info("TRANSACTION SUCCESSFUL. "+request.getAmount()+" USD sent from "+senderCustomer.getFullName()+" to "+receiverCustomer.getFullName());

                customerService.save(receiverCustomer);
                apiResponse.setResponseCode("00");
                apiResponse.setResponseMessage("Transfert Reussi");
                Sms sms2 = new Sms();
                sms2.setTo(receiverCustomer.getPhoneNumber());
                sms2.setMessage("transaction confirmee. vous avez recu "+originalAmount+"USD venant de "+senderCustomer.getFullName()+" USD via PesaPay."+
                        " type de transaction C2C." +
                        " votre solde actuel est de "+receiverCustomer.getBalance().setScale(2,BigDecimal.ROUND_UP)+
                        " USD. numero de transaction "+transaction.getTransactionNumber());
                SmsService.sendSms(sms2);

               }

        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
