package com.zara.Zara.controllers;

import com.zara.Zara.entities.AppUser;
import com.zara.Zara.entities.Transaction;
import com.zara.Zara.models.TransactionRequestBody;
import com.zara.Zara.services.ISettingService;
import com.zara.Zara.services.ITransactionService;
import com.zara.Zara.services.IUserService;
import com.zara.Zara.utils.SendSms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import static com.zara.Zara.constants.ConstantVariables.*;
import static com.zara.Zara.constants.Keys.*;
import static com.zara.Zara.constants.Responses.*;
import static com.zara.Zara.utils.BusinessNumbersGenerator.generateTransationNumber;
import static com.zara.Zara.utils.CheckingUtils.isAccountLocked;
import static com.zara.Zara.utils.CheckingUtils.isAccountVerified;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    IUserService userService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    ISettingService settingService;
    Logger LOGGER = LogManager.getLogger(TransactionController.class);
    HttpHeaders responseHeaders = new HttpHeaders();
    String senderNumber,receiverNumber,senderPin;
    Double amnt;
    AppUser senderUser;
    AppUser receiverUser;
    Double senderExistingBalance;
    Double receiverExistingBalance;
    Double updatedSenderBalance;
    Double updatedReceiverBalance;

    @PostMapping("/adminTransfer")
    public ResponseEntity<?> adminTransfer(
            @RequestBody TransactionRequestBody body) throws UnsupportedEncodingException {
        if(body.getSender().startsWith("+")){
            senderNumber=body.getSender().substring(1);
        }
        else{
            senderNumber=body.getSender();
        }

        if(body.getReceiver().startsWith("+")){
            receiverNumber=body.getReceiver().substring(1);
        }
        else{
            receiverNumber=body.getReceiver();
        }
      senderPin = body.getPin();
      amnt = Double.parseDouble(body.getAmount());

        LOGGER.info(senderNumber.trim() + " " + receiverNumber + " " + senderPin + " " + amnt);
       senderUser = userService.findByAccountNumber(senderNumber);

            if (senderUser == null) {
                responseHeaders.set(RESPONSE_CODE, RESPONSE_SUCCESS);
                responseHeaders.set(RESPONSE_MESSAGE, YOUR_ACCOUNT_IS_NOT_FOUND);
                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
            }
            LOGGER.info("...................acc no " + senderNumber);
            if (isAccountVerified(senderNumber, userService)) {
                if(!isAccountLocked(senderNumber,userService)){
                    if (bCryptPasswordEncoder.matches(senderPin, senderUser.getPin())) {
                        if (!receiverNumber.equals(senderUser.getAccountNumber())
                                && !receiverNumber.equals(senderUser.getPhone())) {

                            receiverUser = userService.findByAccountNumber(receiverNumber);
                            if (receiverUser == null) {
                                responseHeaders.set(RESPONSE_CODE, RESPONSE_SUCCESS);
                                responseHeaders.set(RESPONSE_MESSAGE, USER_NOT_FOUND);
                                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                            }
                            senderExistingBalance = senderUser.getBalance();
                            receiverExistingBalance = receiverUser.getBalance();
                            if (senderExistingBalance >= amnt) {

                                Transaction transaction = new Transaction();
                                transaction.setCreatedOn(new Date());
                                transaction.setTransactionType(TRANSACTION_ADMIN_TRANSFERT);
                                transaction.setCreatedBy(senderUser);
                                transaction.setReceiver(receiverUser);
                                transaction.setAmount(amnt);
                                transaction.setTransactionNumber(generateTransationNumber(transactionService));
                                updatedSenderBalance = senderExistingBalance - amnt;
                                senderUser.setBalance(updatedSenderBalance);

                                updatedReceiverBalance = receiverExistingBalance + amnt;
                                receiverUser.setBalance(updatedReceiverBalance);

                                transactionService.addTransaction(transaction);
                                userService.addUser(receiverUser);
                                AppUser updatedSender = userService.addUser(senderUser);
                                AppUser updatedReceiver = userService.addUser(receiverUser);

                                // TODO: 07/07/2018 SEND SMS TO BOTH THE SENDER AND THE RECEIVER


                                String messageToSender = TRANSACTION_NUMBER + transaction.getTransactionNumber() + ". " + DEAR + senderUser.getFullName() + YOU_HAVE_SENT + amnt +" $" + TO +
                                        receiverUser.getFullName() + ON + transaction.getCreatedOn().toString() + ". " + YOUR_NEW_BALANCE_IS +
                                        updatedSender.getBalance() + " $";

                                String messageToReceiver = TRANSACTION_NUMBER + transaction.getTransactionNumber() + ". " + DEAR + receiverUser.getFullName() + YOU_HAVE_RECEIVED + amnt+" $" + FROM +
                                        senderUser.getFullName() + ON + transaction.getCreatedOn().toString() + ". " + YOUR_NEW_BALANCE_IS +
                                        updatedReceiver.getBalance() + " $";

                                SendSms.send("+"+senderUser.getPhone(), messageToSender, settingService);
                                SendSms.send("+"+receiverUser.getPhone(), messageToReceiver,settingService);
                                responseHeaders.set(RESPONSE_CODE, RESPONSE_SUCCESS);
                                responseHeaders.set(RESPONSE_MESSAGE, messageToSender);
                                return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);

                            } else {
                                responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                                responseHeaders.set(RESPONSE_MESSAGE, INSUFICIENT_FUNDS + " " + senderUser.getBalance() + "$");
                                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                            }
                        } else {
                            LOGGER.info("....CANNOT SEND MONEY TO YOURSELF....");
                            responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                            responseHeaders.set(RESPONSE_MESSAGE, CANNOT_SEND_MONEY_YOUR_OWN_ACC);
                            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                        }

                    } else {
                        LOGGER.info("....WRONG PIN....");
                        responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                        responseHeaders.set(RESPONSE_MESSAGE, "Wrong Pin");
                        return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                    }
                }
                else
                {
                    responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                    responseHeaders.set(RESPONSE_MESSAGE, ACCOUNT_LOCKED);
                    return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                }
            } else {
                responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                responseHeaders.set(RESPONSE_MESSAGE, ACCOUNT_NOT_VERIFIED);
                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
            }


    }

    @PostMapping("/sending")
    public ResponseEntity<?> sending(
            @RequestBody TransactionRequestBody body) throws UnsupportedEncodingException {
        if(body.getSender().startsWith("+")){
            senderNumber=body.getSender().substring(1);
        }
        else{
            senderNumber=body.getSender();
        }

        if(body.getReceiver().startsWith("+")){
            receiverNumber=body.getReceiver().substring(1);
        }
        else{
            receiverNumber=body.getReceiver();
        }
         senderPin = body.getPin();
         amnt = Double.parseDouble(body.getAmount());
        senderUser = userService.findByAccountNumber(senderNumber);

            if (senderUser == null) {
                responseHeaders.set(RESPONSE_CODE, RESPONSE_SUCCESS);
                responseHeaders.set(RESPONSE_MESSAGE, YOUR_ACCOUNT_IS_NOT_FOUND);
                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
            }
            LOGGER.info("...................acc no " + senderNumber);
            if (isAccountVerified(senderNumber, userService)) {
                if (!isAccountLocked(senderNumber,userService))
                {
                    if (bCryptPasswordEncoder.matches(senderPin, senderUser.getPin())) {
                        if (!receiverNumber.equals(senderUser.getAccountNumber())
                                && !receiverNumber.equals(senderUser.getPhone())) {

                            receiverUser = userService.findByAccountNumber(receiverNumber);
                            if (receiverUser == null) {
                                responseHeaders.set(RESPONSE_CODE, RESPONSE_SUCCESS);
                                responseHeaders.set(RESPONSE_MESSAGE, USER_NOT_FOUND);
                                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                            }
                            senderExistingBalance = senderUser.getBalance();
                            receiverExistingBalance = receiverUser.getBalance();
                            if (senderExistingBalance >= amnt) {

                                Transaction transaction = new Transaction();
                                transaction.setCreatedOn(new Date());
                                transaction.setTransactionType(TRANSACTION_SEND);
                                transaction.setCreatedBy(senderUser);
                                transaction.setReceiver(receiverUser);
                                transaction.setAmount(amnt);
                                transaction.setTransactionNumber(generateTransationNumber(transactionService));
                                updatedSenderBalance = senderExistingBalance - amnt;
                                senderUser.setBalance(updatedSenderBalance);

                                updatedReceiverBalance = receiverExistingBalance + amnt;
                                receiverUser.setBalance(updatedReceiverBalance);

                                transactionService.addTransaction(transaction);
                                userService.addUser(receiverUser);
                                AppUser updatedSender = userService.addUser(senderUser);
                                AppUser updatedReceiver = userService.addUser(receiverUser);

                                // TODO: 07/07/2018 SEND SMS TO BOTH THE SENDER AND THE RECEIVER


                                String messageToSender = TRANSACTION_NUMBER + transaction.getTransactionNumber() + ". " + DEAR + senderUser.getFullName() + YOU_HAVE_SENT + amnt +" $" + TO +
                                        receiverUser.getFullName() + ON + transaction.getCreatedOn().toString() + ". " + YOUR_NEW_BALANCE_IS +
                                        updatedSender.getBalance() + " $";

                                String messageToReceiver = TRANSACTION_NUMBER + transaction.getTransactionNumber() + ". " + DEAR + receiverUser.getFullName() + YOU_HAVE_RECEIVED + amnt+" $" + FROM +
                                        senderUser.getFullName() + ON + transaction.getCreatedOn().toString() + ". " + YOUR_NEW_BALANCE_IS +
                                        updatedReceiver.getBalance() + " $";
                                SendSms.send(senderUser.getPhone(), messageToSender,settingService);
                                SendSms.send(receiverUser.getPhone(), messageToReceiver,settingService);
                                responseHeaders.set(RESPONSE_CODE, RESPONSE_SUCCESS);
                                responseHeaders.set(RESPONSE_MESSAGE, messageToSender);
                                return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);

                            } else {
                                responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                                responseHeaders.set(RESPONSE_MESSAGE, INSUFICIENT_FUNDS + " " + senderUser.getBalance() + "$");
                                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                            }
                        } else {
                            LOGGER.info("....CANNOT SEND MONEY TO YOURSELF....");
                            responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                            responseHeaders.set(RESPONSE_MESSAGE, CANNOT_SEND_MONEY_YOUR_OWN_ACC);
                            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                        }

                    } else {
                        LOGGER.info("....WRONG PIN....");
                        responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                        responseHeaders.set(RESPONSE_MESSAGE, "Wrong Pin");
                        return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                    }
                }
                    else{
                    responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                    responseHeaders.set(RESPONSE_MESSAGE, ACCOUNT_LOCKED);
                    return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                }
            } else {
                responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                responseHeaders.set(RESPONSE_MESSAGE, ACCOUNT_NOT_VERIFIED);
                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
            }


    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @RequestBody TransactionRequestBody body) throws UnsupportedEncodingException {

        if(body.getSender().startsWith("+")){
            senderNumber = body.getSender().substring(1);
        }
        else{
            senderNumber = body.getSender();
        }
        receiverNumber = body.getAgentNumber();
        senderPin = body.getPin();
        amnt = Double.parseDouble(body.getAmount());

        senderUser = userService.findByAccountNumber(senderNumber);
        receiverUser = userService.findByAgentNumber(body.getAgentNumber());
        if (senderUser == null) {
            responseHeaders.set(RESPONSE_MESSAGE, YOUR_ACCOUNT_IS_NOT_FOUND);
            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
        }
        if (receiverUser.isAgentAccountLocked()) {
            responseHeaders.set(RESPONSE_MESSAGE, AGENT_LOCKED);
            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
        }
        LOGGER.info("...................acc no " + senderNumber);
        if (isAccountVerified(senderNumber, userService)) {
            if(!isAccountLocked(senderNumber,userService)){
                if (bCryptPasswordEncoder.matches(senderPin, senderUser.getPin())) {
                    if (!receiverNumber.equals(senderUser.getAgentNumber())
                            && !receiverNumber.equals(senderUser.getPhone())) {


                        if (receiverUser == null) {
                            responseHeaders.set(RESPONSE_CODE, RESPONSE_SUCCESS);
                            responseHeaders.set(RESPONSE_MESSAGE, AGENT_NOT_FOUND);
                            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                        }
                        senderExistingBalance = senderUser.getBalance();
                        receiverExistingBalance = receiverUser.getBalance();
                        if (senderExistingBalance >= amnt) {

                            Transaction transaction = new Transaction();
                            transaction.setCreatedOn(new Date());
                            transaction.setTransactionType(TRANSACTION_WITHDRAWAL);
                            transaction.setCreatedBy(senderUser);
                            transaction.setReceiver(receiverUser);
                            transaction.setAmount(amnt);
                            transaction.setTransactionNumber(generateTransationNumber(transactionService));
                            updatedSenderBalance = senderExistingBalance - amnt;
                            senderUser.setBalance(updatedSenderBalance);

                            updatedReceiverBalance = receiverExistingBalance + amnt;
                            receiverUser.setBalance(updatedReceiverBalance);

                            transactionService.addTransaction(transaction);
                            userService.addUser(receiverUser);
                            AppUser updatedSender = userService.addUser(senderUser);
                            AppUser updatedReceiver = userService.addUser(receiverUser);

                            // TODO: 07/07/2018 SEND SMS TO BOTH THE SENDER AND THE RECEIVER


                            String messageToSender = TRANSACTION_NUMBER + transaction.getTransactionNumber() + ". " + DEAR + senderUser.getFullName() + YOU_HAVE_WITHDRAWN + amnt +" $" + TO +
                                    receiverUser.getFullName() + ON + transaction.getCreatedOn().toString() + ". " + YOUR_NEW_BALANCE_IS +
                                    updatedSender.getBalance() + " $";

                            String messageToReceiver = TRANSACTION_NUMBER + transaction.getTransactionNumber() + ". " + DEAR + receiverUser.getFullName() + YOU_HAVE_RECEIVED + amnt+" $" + FROM +
                                    receiverUser.getFullName() + ON + transaction.getCreatedOn().toString() + ". " + YOUR_NEW_BALANCE_IS +
                                    updatedReceiver.getBalance() + " $";
                            SendSms.send(senderUser.getPhone(), messageToSender,settingService);
                            SendSms.send(receiverUser.getPhone(), messageToReceiver,settingService);
                            responseHeaders.set(RESPONSE_CODE, RESPONSE_SUCCESS);
                            responseHeaders.set(RESPONSE_MESSAGE, messageToSender);
                            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);

                        } else {
                            responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                            responseHeaders.set(RESPONSE_MESSAGE, INSUFICIENT_FUNDS + " " + senderUser.getBalance() + "$");
                            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        LOGGER.info("....CANNOT SEND MONEY TO YOURSELF....");
                        responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                        responseHeaders.set(RESPONSE_MESSAGE, CANNOT_SEND_MONEY_YOUR_OWN_ACC);
                        return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                    }

                } else {
                    LOGGER.info("....WRONG PIN....");
                    responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                    responseHeaders.set(RESPONSE_MESSAGE, "Wrong Pin");
                    return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                }
            }
            else {
                responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                responseHeaders.set(RESPONSE_MESSAGE, ACCOUNT_LOCKED);
                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
            }
        } else {
            responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
            responseHeaders.set(RESPONSE_MESSAGE, ACCOUNT_NOT_VERIFIED);
            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
        }

    }


    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(
            @RequestBody TransactionRequestBody body) throws UnsupportedEncodingException {

        LOGGER.info(".............REQUEST BODY: "+body.toString());
        senderNumber = body.getAgentNumber();
        if(body.getReceiver().startsWith("+")){
            receiverNumber = body.getReceiver().substring(1);
        }
        else{
            receiverNumber = body.getReceiver();
        }
        senderPin = body.getPin();
        amnt = Double.parseDouble(body.getAmount());
        senderUser = userService.findByAgentNumber(senderNumber);
            if (senderUser == null) {
                responseHeaders.set(RESPONSE_CODE, RESPONSE_SUCCESS);
                responseHeaders.set(RESPONSE_MESSAGE, YOUR_ACCOUNT_IS_NOT_FOUND);
                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
            }

             if (senderUser.isAgentAccountLocked()) {
            responseHeaders.set(RESPONSE_MESSAGE, YOU_NO_LONGER_AN_AGENT);
            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
        }
            LOGGER.info("...................acc no " + senderNumber);
            if (isAccountVerified(senderUser.getPhone(), userService)) {
                if(!isAccountLocked(senderUser.getPhone(), userService)){
                    if (bCryptPasswordEncoder.matches(senderPin, senderUser.getPin())) {
                        if (!receiverNumber.equals(senderUser.getAccountNumber())
                                && !receiverNumber.equals(senderUser.getPhone())) {

                            receiverUser = userService.findByAccountNumber(receiverNumber);
                            if (receiverUser == null) {
                                responseHeaders.set(RESPONSE_CODE, RESPONSE_SUCCESS);
                                responseHeaders.set(RESPONSE_MESSAGE, USER_NOT_FOUND);
                                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                            }
                            senderExistingBalance = senderUser.getBalance();
                            receiverExistingBalance = receiverUser.getBalance();
                            if (senderExistingBalance >= amnt) {

                                Transaction transaction = new Transaction();
                                transaction.setCreatedOn(new Date());
                                transaction.setTransactionType(TRANSACTION_DEPOSIT);
                                transaction.setCreatedBy(senderUser);
                                transaction.setReceiver(receiverUser);
                                transaction.setAmount(amnt);
                                transaction.setTransactionNumber(generateTransationNumber(transactionService));
                                updatedSenderBalance = senderExistingBalance - amnt;
                                senderUser.setBalance(updatedSenderBalance);

                                updatedReceiverBalance = receiverExistingBalance + amnt;
                                receiverUser.setBalance(updatedReceiverBalance);

                                transactionService.addTransaction(transaction);
                                userService.addUser(receiverUser);
                                AppUser updatedSender = userService.addUser(senderUser);
                                AppUser updatedReceiver = userService.addUser(receiverUser);

                                // TODO: 07/07/2018 SEND SMS TO BOTH THE SENDER AND THE RECEIVER


                                String messageToSender = TRANSACTION_NUMBER + transaction.getTransactionNumber() + ". " + DEAR + senderUser.getFullName() + YOU_HAVE_SENT+ amnt +" $" + TO +
                                        receiverUser.getFullName() + ON + transaction.getCreatedOn().toString() + ". " + YOUR_NEW_BALANCE_IS +
                                        updatedSender.getBalance() + " $";

                                String messageToReceiver = TRANSACTION_NUMBER + transaction.getTransactionNumber() + ". " + DEAR + receiverUser.getFullName() + YOU_HAVE_RECEIVED + amnt+" $" + FROM +
                                        receiverUser.getFullName() + ON + transaction.getCreatedOn().toString() + ". " + YOUR_NEW_BALANCE_IS +
                                        updatedReceiver.getBalance() + " $";
                                SendSms.send(senderUser.getPhone(), messageToSender,settingService);
                                SendSms.send(receiverUser.getPhone(), messageToReceiver,settingService);
                                responseHeaders.set(RESPONSE_CODE, RESPONSE_SUCCESS);
                                responseHeaders.set(RESPONSE_MESSAGE, messageToSender);
                                return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);

                            } else {
                                responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                                responseHeaders.set(RESPONSE_MESSAGE, INSUFICIENT_FUNDS + " " + senderUser.getBalance() + "$");
                                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                            }
                        } else {
                            LOGGER.info("....CANNOT SEND MONEY TO YOURSELF....");
                            responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                            responseHeaders.set(RESPONSE_MESSAGE, CANNOT_SEND_MONEY_YOUR_OWN_ACC);
                            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                        }

                    } else {
                        LOGGER.info("....WRONG PIN....");
                        responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                        responseHeaders.set(RESPONSE_MESSAGE, "Wrong Pin");
                        return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                    }
                }
                else{
                    responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                    responseHeaders.set(RESPONSE_MESSAGE, ACCOUNT_LOCKED);
                    return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                }
            } else {
                responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                responseHeaders.set(RESPONSE_MESSAGE, ACCOUNT_NOT_VERIFIED);
                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
            }


    }

    @PostMapping("/onlinePayment")
    public ResponseEntity<?> onlinePayment(
            @RequestBody TransactionRequestBody body) throws UnsupportedEncodingException {

        senderNumber = URLDecoder.decode(body.getSender(), "UTF-8");
        receiverNumber = URLDecoder.decode(body.getReceiver(), "UTF-8");
        senderPin = body.getPin();
       amnt = Double.parseDouble(body.getAmount());
        senderUser = userService.findByAccountNumber(senderNumber.substring(1));

            if (senderUser == null) {
                responseHeaders.set(RESPONSE_CODE, RESPONSE_SUCCESS);
                responseHeaders.set(RESPONSE_MESSAGE, YOUR_ACCOUNT_IS_NOT_FOUND);
                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
            }
            LOGGER.info("...................acc no " + senderNumber);
            if (isAccountVerified(senderNumber, userService)) {
                if (bCryptPasswordEncoder.matches(senderPin, senderUser.getPin())) {
                    if (!receiverNumber.equals(senderUser.getAccountNumber())
                            && !receiverNumber.equals(senderUser.getPhone().substring(1))) {

                        receiverUser = userService.findByAccountNumber(receiverNumber.substring(1));
                        if (receiverUser == null) {
                            responseHeaders.set(RESPONSE_CODE, RESPONSE_SUCCESS);
                            responseHeaders.set(RESPONSE_MESSAGE, USER_NOT_FOUND);
                            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                        }
                        senderExistingBalance = senderUser.getBalance();
                        receiverExistingBalance = receiverUser.getBalance();

                        if (senderExistingBalance >= amnt) {

                            Transaction transaction = new Transaction();
                            transaction.setCreatedOn(new Date());
                            transaction.setTransactionType(TRANSACTION_PAYMENT);
                            transaction.setCreatedBy(senderUser);
                            transaction.setReceiver(receiverUser);
                            transaction.setAmount(amnt);
                            transaction.setDescription(body.getDescription());
                            transaction.setTransactionNumber(generateTransationNumber(transactionService));
                            updatedSenderBalance = senderExistingBalance - amnt;
                            senderUser.setBalance(updatedSenderBalance);

                            updatedReceiverBalance = receiverExistingBalance + amnt;
                            receiverUser.setBalance(updatedReceiverBalance);

                            transactionService.addTransaction(transaction);
                            userService.addUser(receiverUser);
                            AppUser updatedSender = userService.addUser(senderUser);
                            AppUser updatedReceiver = userService.addUser(receiverUser);

                            // TODO: 07/07/2018 SEND SMS TO BOTH THE SENDER AND THE RECEIVER


                            String responseToSend = TRANSACTION_NUMBER + transaction.getTransactionNumber() + ". " + DEAR + senderUser.getFullName() + YOU_HAVE_SENT + amnt + TO +
                                    receiverUser.getFullName() + ON + transaction.getCreatedOn().toString() + ". " + YOUR_NEW_BALANCE_IS +
                                    updatedSender.getBalance() + " $";
                            responseHeaders.set(RESPONSE_CODE, RESPONSE_SUCCESS);
                            responseHeaders.set(RESPONSE_MESSAGE, responseToSend);
                            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);

                        } else {
                            responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                            responseHeaders.set(RESPONSE_MESSAGE, INSUFICIENT_FUNDS + " " + senderUser.getBalance() + "$");
                            return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        LOGGER.info("....CANNOT SEND MONEY TO YOURSELF....");
                        responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                        responseHeaders.set(RESPONSE_MESSAGE, CANNOT_SEND_MONEY_YOUR_OWN_ACC);
                        return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                    }

                } else {
                    LOGGER.info("....WRONG PIN....");
                    responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                    responseHeaders.set(RESPONSE_MESSAGE, "Wrong Pin");
                    return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
                }
            } else {
                responseHeaders.set(RESPONSE_CODE, RESPONSE_FAILURE);
                responseHeaders.set(RESPONSE_MESSAGE, ACCOUNT_NOT_VERIFIED);
                return new ResponseEntity<>(responseHeaders, HttpStatus.BAD_REQUEST);
            }

    }


    @GetMapping("/ministatement/{id}")
    public ResponseEntity<?> getMiniStatement(@PathVariable Long id) {

        return ResponseEntity.status(200).body(transactionService.getMiniStatement(id));

    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(transactionService.getAll());
    }

}
