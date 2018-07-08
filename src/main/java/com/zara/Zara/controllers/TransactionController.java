package com.zara.Zara.controllers;

import com.zara.Zara.models.AppUser;
import com.zara.Zara.models.Transaction;
import com.zara.Zara.services.ITransactionService;
import com.zara.Zara.services.IUserService;
import com.zara.Zara.utils.GenerateRandomStuff;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Optional;

import static com.zara.Zara.constants.ConstantVariables.*;
import static com.zara.Zara.constants.Keys.*;
import static com.zara.Zara.constants.Responses.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    IUserService userService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    Logger LOGGER = LogManager.getLogger(TransactionController.class);
    @PostMapping("/sending")
    public ResponseEntity<?> sending(HttpServletRequest request, HttpServletResponse response){
        AppUser senderUser;
        AppUser receiverUser;
        String senderAccountNumber = request.getHeader("senderAccountNumber");
        String receiverAccountNumber = request.getHeader("receiverAccountNumber");
        String senderPin = request.getHeader("senderPin");
        String amount = request.getHeader("amount");
        Double senderExistingBalance;
        Double receiverExistingBalance;
        Double sendingAmount;
        Double updatedSenderBalance;
        Double updatedReceiverBalance;



        Optional<AppUser>appUser = Optional.of(userService.findByAccountNumber(senderAccountNumber));
        if(appUser.isPresent()){
           senderUser = appUser.get();

           LOGGER.info("...................acc no "+senderAccountNumber);
           if(isAccountVerified(senderAccountNumber)){
               if (bCryptPasswordEncoder.matches(senderPin, senderUser.getPin())){
                   if(!receiverAccountNumber.equals(senderUser.getAccountNumber())){

                       receiverUser = userService.findByAccountNumber(receiverAccountNumber);
                       senderExistingBalance = senderUser.getBalance();
                       receiverExistingBalance = receiverUser.getBalance();
                       sendingAmount = Double.parseDouble(amount);
                       if(senderExistingBalance>=sendingAmount){

                           Transaction transaction = new Transaction();
                           transaction.setCreatedOn(new Date());
                           transaction.setTransactionType(TRANSACTION_SEND);
                           transaction.setCreatedBy(senderUser);
                           transaction.setReceiver(receiverUser);
                           transaction.setAmount(sendingAmount);
                           transaction.setTransactionNumber(generateTransationNumber());
                           updatedSenderBalance = senderExistingBalance-sendingAmount;
                           senderUser.setBalance(updatedSenderBalance);

                           updatedReceiverBalance = receiverExistingBalance+sendingAmount;
                           receiverUser.setBalance(updatedReceiverBalance);

                           transactionService.addTransaction(transaction);
                           userService.addUser(receiverUser);
                           AppUser updatedSender =userService.addUser(senderUser);
                           AppUser updatedReceiver =userService.addUser(receiverUser);
                           response.addHeader(RESPONSE_CODE, RESPONSE_SUCCESS);
                           response.addHeader(RESPONSE_MESSAGE, SENDIND_SUCCEEDED);
                           LOGGER.info("transaction number "+transaction.getTransactionNumber()+"\n Dear "+senderUser.getFullName()+ " You have sent "+sendingAmount + " to "+
                                   receiverUser.getFullName()+ " on "+ transaction.getCreatedOn().toString()+"\n your new balance is "+
                                   updatedSender.getBalance()+" USD\n");

                           // TODO: 07/07/2018 SEND SMS TO BOTH THE SENDER AND THE RECEIVER 

                           LOGGER.info("transaction number "+transaction.getTransactionNumber()+"\n Dear "+receiverUser.getFullName()+ " You have received "+sendingAmount + " from "+
                                   senderUser.getFullName()+ " on "+ transaction.getCreatedOn().toString()+"\n your new balance is "+
                                   updatedReceiver.getBalance()+" USD\n");
                           return ResponseEntity.status(201).body(transaction);

                       }else{
                           response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
                           response.addHeader(RESPONSE_MESSAGE, INSUFICIENT_FUNDS);
                       }
                   }else{
                       response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
                       response.addHeader(RESPONSE_MESSAGE, IMPOSSIBLE_OPERATION);
                   }

               }else{
                   response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
                   response.addHeader(RESPONSE_MESSAGE, INCORRECT_PIN);
               }
           }
           else{
               response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
               response.addHeader(RESPONSE_MESSAGE, ACCOUNT_NOT_VERIFIED);
           }

        }
        else{
            response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
            response.addHeader(RESPONSE_MESSAGE, USER_NOT_FOUND);
        }

        return null;
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(HttpServletRequest request, HttpServletResponse response){
        AppUser senderUser;
        AppUser receiverUser;
        String senderAccountNumber = request.getHeader("senderAccountNumber");
        String receiverAccountNumber = request.getHeader("receiverAccountNumber");
        String senderPin = request.getHeader("senderPin");
        String amount = request.getHeader("amount");
        Double senderExistingBalance;
        Double receiverExistingBalance;
        Double sendingAmount;
        Double updatedSenderBalance;
        Double updatedReceiverBalance;



        Optional<AppUser>appUser = Optional.of(userService.findByAccountNumber(senderAccountNumber));
        if(appUser.isPresent()){
            senderUser = appUser.get();

            LOGGER.info("...................acc no "+senderAccountNumber);
            if(isAccountVerified(senderAccountNumber)){
                if (bCryptPasswordEncoder.matches(senderPin, senderUser.getPin())){
                    if(!receiverAccountNumber.equals(senderUser.getAccountNumber())){

                        receiverUser = userService.findByAccountNumber(receiverAccountNumber);
                        senderExistingBalance = senderUser.getBalance();
                        receiverExistingBalance = receiverUser.getBalance();
                        sendingAmount = Double.parseDouble(amount);
                        if(senderExistingBalance>=sendingAmount){

                            Transaction transaction = new Transaction();
                            transaction.setCreatedOn(new Date());
                            transaction.setTransactionType(TRANSACTION_DEPOSIT);
                            transaction.setCreatedBy(senderUser);
                            transaction.setReceiver(receiverUser);
                            transaction.setAmount(sendingAmount);
                            transaction.setTransactionNumber(generateTransationNumber());
                            updatedSenderBalance = senderExistingBalance-sendingAmount;
                            senderUser.setBalance(updatedSenderBalance);

                            updatedReceiverBalance = receiverExistingBalance+sendingAmount;
                            receiverUser.setBalance(updatedReceiverBalance);

                            transactionService.addTransaction(transaction);
                            userService.addUser(receiverUser);
                            AppUser updatedSender =userService.addUser(senderUser);
                            AppUser updatedReceiver =userService.addUser(receiverUser);
                            response.addHeader(RESPONSE_CODE, RESPONSE_SUCCESS);
                            response.addHeader(RESPONSE_MESSAGE, SENDIND_SUCCEEDED);
                            LOGGER.info("transaction number "+transaction.getTransactionNumber()+"\n Dear "+senderUser.getFullName()+ " You have sent "+sendingAmount + " to "+
                                    receiverUser.getFullName()+ " on "+ transaction.getCreatedOn().toString()+"\n your new balance is "+
                                    updatedSender.getBalance()+" USD\n");
                            // TODO: 07/07/2018 SEND SMS TO BOTH THE SENDER AND THE RECEIVER  
                            LOGGER.info("transaction number "+transaction.getTransactionNumber()+"\n Dear "+receiverUser.getFullName()+ " You have received "+sendingAmount + " from "+
                                    senderUser.getFullName()+ " on "+ transaction.getCreatedOn().toString()+"\n your new balance is "+
                                    updatedReceiver.getBalance()+" USD\n");
                            return ResponseEntity.status(201).body(transaction);

                        }else{
                            response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
                            response.addHeader(RESPONSE_MESSAGE, INSUFICIENT_FUNDS);
                        }
                    }else{
                        response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
                        response.addHeader(RESPONSE_MESSAGE, IMPOSSIBLE_OPERATION);
                    }

                }else{
                    response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
                    response.addHeader(RESPONSE_MESSAGE, INCORRECT_PIN);
                }
            }
            else{
                response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
                response.addHeader(RESPONSE_MESSAGE, ACCOUNT_NOT_VERIFIED);
            }

        }
        else{
            response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
            response.addHeader(RESPONSE_MESSAGE, USER_NOT_FOUND);
        }

        return null;
    }

    @PostMapping("/onlinePayment")
    public ResponseEntity<?> onlinePayment(HttpServletRequest request, HttpServletResponse response){
        AppUser senderUser;
        AppUser receiverUser;
        String senderAccountNumber = request.getHeader("senderAccountNumber");
        String receiverAccountNumber = request.getHeader("receiverAccountNumber");
        String description = request.getHeader("description");
        String senderPin = request.getHeader("senderPin");
        String amount = request.getHeader("amount");
        Double senderExistingBalance;
        Double receiverExistingBalance;
        Double sendingAmount;
        Double updatedSenderBalance;
        Double updatedReceiverBalance;



        Optional<AppUser>appUser = Optional.of(userService.findByAccountNumber(senderAccountNumber));
        if(appUser.isPresent()){
            senderUser = appUser.get();

            LOGGER.info("...................acc no "+senderAccountNumber);
            if(isAccountVerified(senderAccountNumber)){
                if (bCryptPasswordEncoder.matches(senderPin, senderUser.getPin())){
                    if(!receiverAccountNumber.equals(senderUser.getAccountNumber())){

                        receiverUser = userService.findByAccountNumber(receiverAccountNumber);
                        senderExistingBalance = senderUser.getBalance();
                        receiverExistingBalance = receiverUser.getBalance();
                        sendingAmount = Double.parseDouble(amount);
                        if(senderExistingBalance>=sendingAmount){

                            Transaction transaction = new Transaction();
                            transaction.setCreatedOn(new Date());
                            transaction.setTransactionType(TRANSACTION_PAYMENT);
                            transaction.setCreatedBy(senderUser);
                            transaction.setReceiver(receiverUser);
                            transaction.setAmount(sendingAmount);
                            transaction.setTransactionNumber(generateTransationNumber());
                            updatedSenderBalance = senderExistingBalance-sendingAmount;
                            senderUser.setBalance(updatedSenderBalance);

                            updatedReceiverBalance = receiverExistingBalance+sendingAmount;
                            receiverUser.setBalance(updatedReceiverBalance);

                            transactionService.addTransaction(transaction);
                            userService.addUser(receiverUser);
                            AppUser updatedSender =userService.addUser(senderUser);
                            AppUser updatedReceiver =userService.addUser(receiverUser);
                            response.addHeader(RESPONSE_CODE, RESPONSE_SUCCESS);
                            response.addHeader(RESPONSE_MESSAGE, SENDIND_SUCCEEDED);
                            LOGGER.info(TRANSACTION_NUMBER+transaction.getTransactionNumber()+"\n"+DEAR+senderUser.getFullName()+ YOU_HAVE_PAID+sendingAmount + TO +
                                    receiverUser.getFullName()+ ON + transaction.getCreatedOn().toString()+FOR+description+"\n"+
                                    updatedSender.getBalance()+" USD\n");

                            // TODO: 07/07/2018 SEND SMS TO BOTH THE SENDER AND THE RECEIVER  
                            LOGGER.info(TRANSACTION_NUMBER+transaction.getTransactionNumber()+"\n"+DEAR+receiverUser.getFullName()+ YOU_HAVE_BEEN_PAID+sendingAmount + BY +
                                    senderUser.getFullName()+ ON + transaction.getCreatedOn().toString()+FOR+description+"\n"+
                                    updatedSender.getBalance()+" USD\n");
                            return ResponseEntity.status(201).body(transaction);

                        }else{
                            response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
                            response.addHeader(RESPONSE_MESSAGE, INSUFICIENT_FUNDS);
                        }
                    }else{
                        response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
                        response.addHeader(RESPONSE_MESSAGE, IMPOSSIBLE_OPERATION);
                    }

                }else{
                    response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
                    response.addHeader(RESPONSE_MESSAGE, INCORRECT_PIN);
                }
            }
            else{
                response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
                response.addHeader(RESPONSE_MESSAGE, ACCOUNT_NOT_VERIFIED);
            }

        }
        else{
            response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
            response.addHeader(RESPONSE_MESSAGE, USER_NOT_FOUND);
        }

        return null;
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(HttpServletRequest request, HttpServletResponse response){
        AppUser senderUser;
        AppUser receiverUser;
        String senderAccountNumber = request.getHeader("senderAccountNumber");
        String agentNumber = request.getHeader("agentNumber");
        String senderPin = request.getHeader("senderPin");
        String amount = request.getHeader("amount");
        Double senderExistingBalance;
        Double receiverExistingBalance;
        Double sendingAmount;
        Double updatedSenderBalance;
        Double updatedReceiverBalance;



        Optional<AppUser>appUser = Optional.of(userService.findByAccountNumber(senderAccountNumber));
        if(appUser.isPresent()){
            senderUser = appUser.get();

            LOGGER.info("...................acc no "+senderAccountNumber);
            if(isAccountVerified(senderAccountNumber)){
                if (bCryptPasswordEncoder.matches(senderPin, senderUser.getPin())){
                    if(!agentNumber.equals(senderUser.getAccountNumber())){

                        receiverUser = (appUser.isPresent()?userService.findByAgentNumber(agentNumber).get():null);
                        senderExistingBalance = senderUser.getBalance();
                        receiverExistingBalance = receiverUser.getBalance();
                        sendingAmount = Double.parseDouble(amount);
                        if(senderExistingBalance>=sendingAmount){

                            Transaction transaction = new Transaction();
                            transaction.setCreatedOn(new Date());
                            transaction.setTransactionType(TRANSACTION_WITHDRAWAL);
                            transaction.setCreatedBy(senderUser);
                            transaction.setReceiver(receiverUser);
                            transaction.setAmount(sendingAmount);
                            transaction.setTransactionNumber(generateTransationNumber());
                            updatedSenderBalance = senderExistingBalance-sendingAmount;
                            senderUser.setBalance(updatedSenderBalance);

                            updatedReceiverBalance = receiverExistingBalance+sendingAmount;
                            receiverUser.setBalance(updatedReceiverBalance);

                            transactionService.addTransaction(transaction);
                            userService.addUser(receiverUser);
                            AppUser updatedSender =userService.addUser(senderUser);
                            AppUser updatedReceiver =userService.addUser(receiverUser);
                            response.addHeader(RESPONSE_CODE, RESPONSE_SUCCESS);
                            response.addHeader(RESPONSE_MESSAGE, SENDIND_SUCCEEDED);
                            LOGGER.info("transaction number "+transaction.getTransactionNumber()+"\n Dear "+senderUser.getFullName()+ " You have withdrawn "+sendingAmount + " from Agent "+
                                    receiverUser.getFullName()+ " on "+ transaction.getCreatedOn().toString()+"\n your new balance is "+
                                    updatedSender.getBalance()+" USD\n");
                            // TODO: 07/07/2018 SEND SMS TO BOTH THE WITHDRAWER AND THE AGENT 
                            LOGGER.info("transaction number "+transaction.getTransactionNumber()+"\n Dear "+receiverUser.getFullName()+ " You have received a withdrawal of   "+sendingAmount + " done by "+
                                    senderUser.getFullName()+ " on "+ transaction.getCreatedOn().toString()+"\n your new balance is "+
                                    updatedReceiver.getBalance()+" USD\n");
                            return ResponseEntity.status(201).body(transaction);

                        }else{
                            response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
                            response.addHeader(RESPONSE_MESSAGE, INSUFICIENT_FUNDS);
                        }
                    }else{
                        response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
                        response.addHeader(RESPONSE_MESSAGE, IMPOSSIBLE_OPERATION);
                    }

                }else{
                    response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
                    response.addHeader(RESPONSE_MESSAGE, INCORRECT_PIN);
                }
            }
            else{
                response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
                response.addHeader(RESPONSE_MESSAGE, ACCOUNT_NOT_VERIFIED);
            }

        }
        else{
            response.addHeader(RESPONSE_CODE, RESPONSE_FAILURE);
            response.addHeader(RESPONSE_MESSAGE, USER_NOT_FOUND);
        }

        return null;
    }

    @GetMapping("/ministatement/{id}")
    public ResponseEntity<?>getMiniStatement(@PathVariable Long id){

        return ResponseEntity.status(200).body(transactionService.getMiniStatement(id));

    }



    public String generateTransationNumber(){
       String transactionNumber = GenerateRandomStuff.getRandomString(3).toUpperCase()
                +String.valueOf(GenerateRandomStuff.getRandomNumber(1000))
                +GenerateRandomStuff.getRandomString(2)
                +String.valueOf(GenerateRandomStuff.getRandomNumber(1000));
        Transaction transaction = transactionService.findByTransactionNumber(transactionNumber);
        if(transaction==null){
            return transactionNumber;

        }
        else{
            generateTransationNumber();
        }
        return null;
    }
    public boolean isAccountVerified(String accountNumber){

        AppUser appUser = userService.findByAccountNumber(accountNumber);
        return appUser.isVerified();
    }


}
