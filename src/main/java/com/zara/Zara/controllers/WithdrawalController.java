package com.zara.Zara.controllers;

import com.zara.Zara.entities.AppUser;
import com.zara.Zara.entities.Transaction;
import com.zara.Zara.models.TransactionRequestBody;
import com.zara.Zara.services.ITransactionService;
import com.zara.Zara.services.IUserService;
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
import static com.zara.Zara.utils.CheckingUtils.isAccountVerified;

@RestController
@RequestMapping("/withdrawals")
public class WithdrawalController {
    @Autowired
    IUserService userService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    Logger LOGGER = LogManager.getLogger(WithdrawalController.class);
    HttpHeaders responseHeaders = new HttpHeaders();
    String senderNumber,receiverNumber,senderPin;
    Double amnt;
    AppUser senderUser;
    AppUser receiverUser;
    Double senderExistingBalance;
    Double receiverExistingBalance;
    Double updatedSenderBalance;
    Double updatedReceiverBalance;


    @PostMapping("/post")
    public ResponseEntity<?> withdraw(
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

                        receiverUser = userService.findByAgentNumber(body.getAgentNumber());
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
                            transaction.setTransactionType(TRANSACTION_ADMIN_TRANSFERT);
                            transaction.setCreatedBy(senderUser);
                            transaction.setReceiver(receiverUser);
                            //transaction.setAmount(amnt);
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


}
