package com.zara.Zara.controllers;

import com.zara.Zara.entities.AppUser;
import com.zara.Zara.entities.Transaction;
import com.zara.Zara.models.TransactionRequestBody;
import com.zara.Zara.services.ITransactionService;
import com.zara.Zara.services.IUserService;
import com.zara.Zara.utils.GenerateRandomStuff;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
    HttpHeaders responseHeaders = new HttpHeaders();
    @PostMapping("/adminTransfer")
    public ResponseEntity<?> adminTransfer(
           @RequestBody TransactionRequestBody body) throws UnsupportedEncodingException {
        AppUser senderUser;
        AppUser receiverUser;
        String senderAccountNumber = URLDecoder.decode(body.getSender(), "UTF-8");;
        String receiverAccountNumber = URLDecoder.decode(body.getReceiver(), "UTF-8");;
        String senderPin = body.getPin();
        String amnt = body.getAmount();
        Double senderExistingBalance;
        Double receiverExistingBalance;
        Double sendingAmount;
        Double updatedSenderBalance;
        Double updatedReceiverBalance;


        LOGGER.info(senderAccountNumber.trim() +" "+receiverAccountNumber+" "+senderPin+" "+amnt);
        Optional<AppUser>appUser = Optional.of(userService.findByAccountNumber(senderAccountNumber));
        if(appUser.isPresent()){
            senderUser = appUser.get();
            if (senderUser==null){
                responseHeaders.set(RESPONSE_CODE,RESPONSE_SUCCESS);
                responseHeaders.set(RESPONSE_MESSAGE, YOUR_ACCOUNT_IS_NOT_FOUND);
                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
            }
            LOGGER.info("...................acc no "+senderAccountNumber);
            if(isAccountVerified(senderAccountNumber)){
                if (bCryptPasswordEncoder.matches(senderPin, senderUser.getPin())){
                    if(!receiverAccountNumber.equals(senderUser.getAccountNumber())
                            &&!receiverAccountNumber.equals(senderUser.getPhone())){

                        receiverUser = userService.findByAccountNumber(receiverAccountNumber);
                        if (receiverUser==null){
                            responseHeaders.set(RESPONSE_CODE,RESPONSE_SUCCESS);
                            responseHeaders.set(RESPONSE_MESSAGE, USER_NOT_FOUND);
                            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                        }
                        senderExistingBalance = senderUser.getBalance();
                        receiverExistingBalance = receiverUser.getBalance();
                        sendingAmount = Double.parseDouble(amnt);
                        if(senderExistingBalance>=sendingAmount){

                            Transaction transaction = new Transaction();
                            transaction.setCreatedOn(new Date());
                            transaction.setTransactionType(TRANSACTION_ADMIN_TRANSFERT);
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

                            // TODO: 07/07/2018 SEND SMS TO BOTH THE SENDER AND THE RECEIVER


                            String responseToSend=TRANSACTION_NUMBER+transaction.getTransactionNumber()+". "+DEAR+senderUser.getFullName()+ YOU_HAVE_SENT+sendingAmount + TO+
                                    receiverUser.getFullName()+ ON + transaction.getCreatedOn().toString()+". "+YOUR_NEW_BALANCE_IS+
                                    updatedSender.getBalance()+" $";
                            responseHeaders.set(RESPONSE_CODE,RESPONSE_SUCCESS);
                            responseHeaders.set(RESPONSE_MESSAGE, responseToSend);
                            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);

                        }else{
                            responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                            responseHeaders.set(RESPONSE_MESSAGE, INSUFICIENT_FUNDS+" "+senderUser.getBalance()+"$");
                            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                        }
                    }else{
                        LOGGER.info("....CANNOT SEND MONEY TO YOURSELF....");
                        responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                        responseHeaders.set(RESPONSE_MESSAGE, CANNOT_SEND_MONEY_YOUR_OWN_ACC);
                        return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                    }

                }else{
                    LOGGER.info("....WRONG PIN....");
                    responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                    responseHeaders.set(RESPONSE_MESSAGE, "Wrong Pin");
                    return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                }
            }
            else{
                responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                responseHeaders.set(RESPONSE_MESSAGE, ACCOUNT_NOT_VERIFIED);
                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
            }

        }
        else{
            responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
            responseHeaders.set(RESPONSE_MESSAGE, USER_NOT_FOUND);
            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
        }

        //return null;
    }
    @GetMapping("/sending")
    public ResponseEntity<?> sending(
            @RequestBody TransactionRequestBody body) throws UnsupportedEncodingException {
        AppUser senderUser;
        AppUser receiverUser;
        String senderAccountNumber = URLDecoder.decode(body.getSender(), "UTF-8");;
        String receiverAccountNumber = URLDecoder.decode(body.getReceiver(), "UTF-8");;
        String senderPin = body.getPin();
        String amnt = body.getAmount();
        Double senderExistingBalance;
        Double receiverExistingBalance;
        Double sendingAmount;
        Double updatedSenderBalance;
        Double updatedReceiverBalance;


        LOGGER.info(senderAccountNumber.trim() +" "+receiverAccountNumber+" "+senderPin+" "+amnt);
        Optional<AppUser>appUser = Optional.of(userService.findByAccountNumber(senderAccountNumber));
        if(appUser.isPresent()){
            senderUser = appUser.get();
            if (senderUser==null){
                responseHeaders.set(RESPONSE_CODE,RESPONSE_SUCCESS);
                responseHeaders.set(RESPONSE_MESSAGE, YOUR_ACCOUNT_IS_NOT_FOUND);
                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
            }
            LOGGER.info("...................acc no "+senderAccountNumber);
            if(isAccountVerified(senderAccountNumber)){
                if (bCryptPasswordEncoder.matches(senderPin, senderUser.getPin())){
                    if(!receiverAccountNumber.equals(senderUser.getAccountNumber())
                            &&!receiverAccountNumber.equals(senderUser.getPhone())){

                        receiverUser = userService.findByAccountNumber(receiverAccountNumber);
                        if (receiverUser==null){
                            responseHeaders.set(RESPONSE_CODE,RESPONSE_SUCCESS);
                            responseHeaders.set(RESPONSE_MESSAGE, USER_NOT_FOUND);
                            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                        }
                        senderExistingBalance = senderUser.getBalance();
                        receiverExistingBalance = receiverUser.getBalance();
                        sendingAmount = Double.parseDouble(amnt);
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

                            // TODO: 07/07/2018 SEND SMS TO BOTH THE SENDER AND THE RECEIVER


                            String responseToSend=TRANSACTION_NUMBER+transaction.getTransactionNumber()+". "+DEAR+senderUser.getFullName()+ YOU_HAVE_SENT+sendingAmount + TO+
                                    receiverUser.getFullName()+ ON + transaction.getCreatedOn().toString()+". "+YOUR_NEW_BALANCE_IS+
                                    updatedSender.getBalance()+" $";
                            responseHeaders.set(RESPONSE_CODE,RESPONSE_SUCCESS);
                            responseHeaders.set(RESPONSE_MESSAGE, responseToSend);
                            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);

                        }else{
                            responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                            responseHeaders.set(RESPONSE_MESSAGE, INSUFICIENT_FUNDS+" "+senderUser.getBalance()+"$");
                            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                        }
                    }else{
                        LOGGER.info("....CANNOT SEND MONEY TO YOURSELF....");
                        responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                        responseHeaders.set(RESPONSE_MESSAGE, CANNOT_SEND_MONEY_YOUR_OWN_ACC);
                        return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                    }

                }else{
                    LOGGER.info("....WRONG PIN....");
                    responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                    responseHeaders.set(RESPONSE_MESSAGE, "Wrong Pin");
                    return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                }
            }
            else{
                responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                responseHeaders.set(RESPONSE_MESSAGE, ACCOUNT_NOT_VERIFIED);
                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
            }

        }
        else{
            responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
            responseHeaders.set(RESPONSE_MESSAGE, USER_NOT_FOUND);
            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
        }

        //return null;
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(
            @RequestBody TransactionRequestBody body) throws UnsupportedEncodingException {
        AppUser senderUser;
        AppUser receiverUser;
        String senderAccountNumber = URLDecoder.decode(body.getSender(), "UTF-8");;
        String receiverAccountNumber = URLDecoder.decode(body.getReceiver(), "UTF-8");;
        String senderPin = body.getPin();
        String amnt = body.getAmount();
        Double senderExistingBalance;
        Double receiverExistingBalance;
        Double sendingAmount;
        Double updatedSenderBalance;
        Double updatedReceiverBalance;


        LOGGER.info(senderAccountNumber.trim() +" "+receiverAccountNumber+" "+senderPin+" "+amnt);
        Optional<AppUser>appUser = Optional.of(userService.findByAccountNumber(senderAccountNumber));
        if(appUser.isPresent()){
            senderUser = appUser.get();
            if (senderUser==null){
                responseHeaders.set(RESPONSE_CODE,RESPONSE_SUCCESS);
                responseHeaders.set(RESPONSE_MESSAGE, YOUR_ACCOUNT_IS_NOT_FOUND);
                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
            }
            LOGGER.info("...................acc no "+senderAccountNumber);
            if(isAccountVerified(senderAccountNumber)){
                if (bCryptPasswordEncoder.matches(senderPin, senderUser.getPin())){
                    if(!receiverAccountNumber.equals(senderUser.getAccountNumber())
                            &&!receiverAccountNumber.equals(senderUser.getPhone())){

                        receiverUser = userService.findByAccountNumber(receiverAccountNumber);
                        if (receiverUser==null){
                            responseHeaders.set(RESPONSE_CODE,RESPONSE_SUCCESS);
                            responseHeaders.set(RESPONSE_MESSAGE, USER_NOT_FOUND);
                            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                        }
                        senderExistingBalance = senderUser.getBalance();
                        receiverExistingBalance = receiverUser.getBalance();
                        sendingAmount = Double.parseDouble(amnt);
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

                            // TODO: 07/07/2018 SEND SMS TO BOTH THE SENDER AND THE RECEIVER


                            String responseToSend=TRANSACTION_NUMBER+transaction.getTransactionNumber()+". "+DEAR+senderUser.getFullName()+ YOU_HAVE_SENT+sendingAmount + TO+
                                    receiverUser.getFullName()+ ON + transaction.getCreatedOn().toString()+". "+YOUR_NEW_BALANCE_IS+
                                    updatedSender.getBalance()+" $";
                            responseHeaders.set(RESPONSE_CODE,RESPONSE_SUCCESS);
                            responseHeaders.set(RESPONSE_MESSAGE, responseToSend);
                            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);

                        }else{
                            responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                            responseHeaders.set(RESPONSE_MESSAGE, INSUFICIENT_FUNDS+" "+senderUser.getBalance()+"$");
                            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                        }
                    }else{
                        LOGGER.info("....CANNOT SEND MONEY TO YOURSELF....");
                        responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                        responseHeaders.set(RESPONSE_MESSAGE, CANNOT_SEND_MONEY_YOUR_OWN_ACC);
                        return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                    }

                }else{
                    LOGGER.info("....WRONG PIN....");
                    responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                    responseHeaders.set(RESPONSE_MESSAGE, "Wrong Pin");
                    return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                }
            }
            else{
                responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                responseHeaders.set(RESPONSE_MESSAGE, ACCOUNT_NOT_VERIFIED);
                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
            }

        }
        else{
            responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
            responseHeaders.set(RESPONSE_MESSAGE, USER_NOT_FOUND);
            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
        }

        //return null;
    }

    @PostMapping("/onlinePayment")
    public ResponseEntity<?> onlinePayment(
            @RequestBody TransactionRequestBody body) throws UnsupportedEncodingException {
        AppUser senderUser;
        AppUser receiverUser;
        String senderAccountNumber = URLDecoder.decode(body.getSender(), "UTF-8");;
        String receiverAccountNumber = URLDecoder.decode(body.getReceiver(), "UTF-8");;
        String senderPin = body.getPin();
        String amnt = body.getAmount();
        Double senderExistingBalance;
        Double receiverExistingBalance;
        Double sendingAmount;
        Double updatedSenderBalance;
        Double updatedReceiverBalance;


        LOGGER.info(senderAccountNumber.trim() +" "+receiverAccountNumber+" "+senderPin+" "+amnt);
        Optional<AppUser>appUser = Optional.of(userService.findByAccountNumber(senderAccountNumber));
        if(appUser.isPresent()){
            senderUser = appUser.get();
            if (senderUser==null){
                responseHeaders.set(RESPONSE_CODE,RESPONSE_SUCCESS);
                responseHeaders.set(RESPONSE_MESSAGE, YOUR_ACCOUNT_IS_NOT_FOUND);
                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
            }
            LOGGER.info("...................acc no "+senderAccountNumber);
            if(isAccountVerified(senderAccountNumber)){
                if (bCryptPasswordEncoder.matches(senderPin, senderUser.getPin())){
                    if(!receiverAccountNumber.equals(senderUser.getAccountNumber())
                            &&!receiverAccountNumber.equals(senderUser.getPhone())){

                        receiverUser = userService.findByAccountNumber(receiverAccountNumber);
                        if (receiverUser==null){
                            responseHeaders.set(RESPONSE_CODE,RESPONSE_SUCCESS);
                            responseHeaders.set(RESPONSE_MESSAGE, USER_NOT_FOUND);
                            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                        }
                        senderExistingBalance = senderUser.getBalance();
                        receiverExistingBalance = receiverUser.getBalance();
                        sendingAmount = Double.parseDouble(amnt);
                        if(senderExistingBalance>=sendingAmount){

                            Transaction transaction = new Transaction();
                            transaction.setCreatedOn(new Date());
                            transaction.setTransactionType(TRANSACTION_PAYMENT);
                            transaction.setCreatedBy(senderUser);
                            transaction.setReceiver(receiverUser);
                            transaction.setAmount(sendingAmount);
                            transaction.setDescription(body.getDescription());
                            transaction.setTransactionNumber(generateTransationNumber());
                            updatedSenderBalance = senderExistingBalance-sendingAmount;
                            senderUser.setBalance(updatedSenderBalance);

                            updatedReceiverBalance = receiverExistingBalance+sendingAmount;
                            receiverUser.setBalance(updatedReceiverBalance);

                            transactionService.addTransaction(transaction);
                            userService.addUser(receiverUser);
                            AppUser updatedSender =userService.addUser(senderUser);
                            AppUser updatedReceiver =userService.addUser(receiverUser);

                            // TODO: 07/07/2018 SEND SMS TO BOTH THE SENDER AND THE RECEIVER


                            String responseToSend=TRANSACTION_NUMBER+transaction.getTransactionNumber()+". "+DEAR+senderUser.getFullName()+ YOU_HAVE_SENT+sendingAmount + TO+
                                    receiverUser.getFullName()+ ON + transaction.getCreatedOn().toString()+". "+YOUR_NEW_BALANCE_IS+
                                    updatedSender.getBalance()+" $";
                            responseHeaders.set(RESPONSE_CODE,RESPONSE_SUCCESS);
                            responseHeaders.set(RESPONSE_MESSAGE, responseToSend);
                            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);

                        }else{
                            responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                            responseHeaders.set(RESPONSE_MESSAGE, INSUFICIENT_FUNDS+" "+senderUser.getBalance()+"$");
                            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                        }
                    }else{
                        LOGGER.info("....CANNOT SEND MONEY TO YOURSELF....");
                        responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                        responseHeaders.set(RESPONSE_MESSAGE, CANNOT_SEND_MONEY_YOUR_OWN_ACC);
                        return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                    }

                }else{
                    LOGGER.info("....WRONG PIN....");
                    responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                    responseHeaders.set(RESPONSE_MESSAGE, "Wrong Pin");
                    return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                }
            }
            else{
                responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                responseHeaders.set(RESPONSE_MESSAGE, ACCOUNT_NOT_VERIFIED);
                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
            }

        }
        else{
            responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
            responseHeaders.set(RESPONSE_MESSAGE, USER_NOT_FOUND);
            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
        }

        //return null;
    }
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @RequestBody TransactionRequestBody body) throws UnsupportedEncodingException {
        AppUser senderUser;
        AppUser receiverUser;
        String senderAccountNumber = URLDecoder.decode(body.getSender(), "UTF-8");;
        String receiverAccountNumber = URLDecoder.decode(body.getReceiver(), "UTF-8");;
        String senderPin = body.getPin();
        String amnt = body.getAmount();
        Double senderExistingBalance;
        Double receiverExistingBalance;
        Double sendingAmount;
        Double updatedSenderBalance;
        Double updatedReceiverBalance;


        LOGGER.info(senderAccountNumber.trim() +" "+receiverAccountNumber+" "+senderPin+" "+amnt);
        Optional<AppUser>appUser = Optional.of(userService.findByAccountNumber(senderAccountNumber));
        if(appUser.isPresent()){
            senderUser = appUser.get();
            if (senderUser==null){
                responseHeaders.set(RESPONSE_CODE,RESPONSE_SUCCESS);
                responseHeaders.set(RESPONSE_MESSAGE, YOUR_ACCOUNT_IS_NOT_FOUND);
                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
            }
            LOGGER.info("...................acc no "+senderAccountNumber);
            if(isAccountVerified(senderAccountNumber)){
                if (bCryptPasswordEncoder.matches(senderPin, senderUser.getPin())){
                    if(!receiverAccountNumber.equals(senderUser.getAccountNumber())
                            &&!receiverAccountNumber.equals(senderUser.getPhone())){

                        receiverUser = userService.findByAgentNumber(body.getAgentNumber());
                        if (receiverUser==null){
                            responseHeaders.set(RESPONSE_CODE,RESPONSE_SUCCESS);
                            responseHeaders.set(RESPONSE_MESSAGE, AGENT_NOT_FOUND);
                            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                        }
                        senderExistingBalance = senderUser.getBalance();
                        receiverExistingBalance = receiverUser.getBalance();
                        sendingAmount = Double.parseDouble(amnt);
                        if(senderExistingBalance>=sendingAmount){

                            Transaction transaction = new Transaction();
                            transaction.setCreatedOn(new Date());
                            transaction.setTransactionType(TRANSACTION_ADMIN_TRANSFERT);
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

                            // TODO: 07/07/2018 SEND SMS TO BOTH THE SENDER AND THE RECEIVER


                            String responseToSend=TRANSACTION_NUMBER+transaction.getTransactionNumber()+". "+DEAR+senderUser.getFullName()+ YOU_HAVE_SENT+sendingAmount + TO+
                                    receiverUser.getFullName()+ ON + transaction.getCreatedOn().toString()+". "+YOUR_NEW_BALANCE_IS+
                                    updatedSender.getBalance()+" $";
                            responseHeaders.set(RESPONSE_CODE,RESPONSE_SUCCESS);
                            responseHeaders.set(RESPONSE_MESSAGE, responseToSend);
                            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);

                        }else{
                            responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                            responseHeaders.set(RESPONSE_MESSAGE, INSUFICIENT_FUNDS+" "+senderUser.getBalance()+"$");
                            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                        }
                    }else{
                        LOGGER.info("....CANNOT SEND MONEY TO YOURSELF....");
                        responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                        responseHeaders.set(RESPONSE_MESSAGE, CANNOT_SEND_MONEY_YOUR_OWN_ACC);
                        return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                    }

                }else{
                    LOGGER.info("....WRONG PIN....");
                    responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                    responseHeaders.set(RESPONSE_MESSAGE, "Wrong Pin");
                    return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                }
            }
            else{
                responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
                responseHeaders.set(RESPONSE_MESSAGE, ACCOUNT_NOT_VERIFIED);
                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
            }

        }
        else{
            responseHeaders.set(RESPONSE_CODE,RESPONSE_FAILURE);
            responseHeaders.set(RESPONSE_MESSAGE, USER_NOT_FOUND);
            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
        }

        //return null;
    }

    @GetMapping("/ministatement/{id}")
    public ResponseEntity<?>getMiniStatement(@PathVariable Long id){

        return ResponseEntity.status(200).body(transactionService.getMiniStatement(id));

    }
    @GetMapping("/getAll")
    public ResponseEntity<?>getAll(){
        return ResponseEntity.status(200).body(transactionService.getAll());
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
