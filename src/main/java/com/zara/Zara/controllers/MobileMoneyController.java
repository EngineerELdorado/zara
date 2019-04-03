package com.zara.Zara.controllers;

import com.stripe.exception.*;
import com.stripe.model.Charge;
import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Business;
import com.zara.Zara.entities.Notification;
import com.zara.Zara.entities.PesapayTransaction;
import com.zara.Zara.models.Sms;
import com.zara.Zara.models.TransactionRequestBody;
import com.zara.Zara.services.IBusinessService;
import com.zara.Zara.services.ICustomerService;
import com.zara.Zara.services.INotificationService;
import com.zara.Zara.services.ITransactionService;
import com.zara.Zara.services.banking.StripeService;
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

import static com.zara.Zara.constants.ConstantVariables.*;

@RestController
@RequestMapping("/mobile-money")
@CrossOrigin(origins = "*")
public class MobileMoneyController {

    ApiResponse apiResponse = new ApiResponse();
    Sms sms = new Sms();
    @Autowired
    ICustomerService customerService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    Logger LOGGER = LogManager.getLogger(CreditCardTransactionController.class);
    @Autowired
    StripeService stripeService;
    Charge charge;
    @Autowired
    IBusinessService businessService;

    @Autowired
    INotificationService notificationService;
    TransactionRequestBody request;
    Business business;

    @PostMapping("/business/withdraw")
    public ResponseEntity<?> pesapayTopaypalBusiness(@RequestBody TransactionRequestBody request,
                                                    @RequestParam String service) throws UnsupportedEncodingException, CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
        business = businessService.findByBusinessNumber(request.getSender());
        this.request=request;
        if (business==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'existe pas");
            LOGGER.info("RECEIVER ACCOUNT NOT FOUND FOR "+request.getReceiver());
        }
        else if (business.getBalance().compareTo(new BigDecimal(request.getAmount()))<0){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Solde insuffisant. vous avez "+business.getBalance()+" USD");
            LOGGER.info("INSUFFICIENT FUNDS"+request.getReceiver());
        }
        else if (!business.getStatus().equals("ACTIVE")){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'est pas actif. veillez contacter le service clientel de PesaPay");
            LOGGER.info("SENDER ACCOUNT NOT ACTIVE FOR "+request.getReceiver());
        }else if (!business.isVerified()){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Votre compte n'est pas encore verifie");
            LOGGER.info("SENDER ACCOUNT NOT VERIFIED FOR "+request.getReceiver());

        }else if (!bCryptPasswordEncoder.matches(request.getPin(), business.getPassword())){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("votre pin est incorrect");
            LOGGER.info("WRONG PIN FOR "+request.getSender());
        }

        else{
            try {

                PesapayTransaction transaction = new PesapayTransaction();
                transaction.setAmount(new BigDecimal(request.getAmount()));
                transaction.setCreatedOn(new Date());
                transaction.setStatus("02");
                transaction.setForPaypalEmail(request.getForPaypalEmail());
                transaction.setDescription("transfer ver(PayPal) en suspens. en destination de paypal au compte "+request.getForPaypalEmail());
                transaction.setTransactionNumber(BusinessNumbersGenerator.generateTransationNumber(transactionService));
                transaction.setCreatedByBusiness(business);
                if (service.equals("mpesa")){
                    transaction.setTransactionType(TRANSACTION_MPESA_WITHRAWAL);
                }else if (service.equals("airtel_money")){
                    transaction.setTransactionType(TRANSACTION_AIRTEL_WITHRAWAL);
                }else if (service.equals("orange_money")){
                    transaction.setTransactionType(TRANSACTION_ORANGE_WITHRAWAL);
                }
                process(transaction);


            }catch (Exception e){
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage(e.getLocalizedMessage());
                LOGGER.info("STRIPE_FAILURE_MESSAGE "+e.getLocalizedMessage());
                e.printStackTrace();
            }

        }
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);

    }


    public void process(PesapayTransaction transaction) throws UnsupportedEncodingException {

        PesapayTransaction createdTransaction = transactionService.addTransaction(transaction);
        if (createdTransaction==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("ECHEC");
            LOGGER.info("TRANSACTION FAILED TO PERSIST TO DATABASE");
        }else {

            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("Transaction Reussie");


            business.setBalance(business.getBalance().subtract(new BigDecimal(request.getAmount())));
            Business updatedBusiness = businessService.save(business);
            Sms sms2 = new Sms();
            sms2.setTo(business.getPhoneNumber());
            Notification notification = new Notification();
            String msg = " Votre transfer PesaPay Pesa vers PayPal est en cours. montant "+request.getAmount()+" USD. la somme sera disponiblea dans votre compte PayPal"+
                    "  "+request.getForPaypalEmail()+
                    " dans moins de 3h. no de transaction "+transaction.getTransactionNumber();
            notification.setBusiness(business);
            notification.setMessage(msg);
            notification.setDate(new Date());
            sms2.setMessage(msg);
            SmsService.sendSms(sms2);
            notificationService.save(notification);
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("TRANSACTION REUSSIE");
        }

    }

}
