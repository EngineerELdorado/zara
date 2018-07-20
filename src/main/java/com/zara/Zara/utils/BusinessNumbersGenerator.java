package com.zara.Zara.utils;

import com.zara.Zara.entities.AppUser;
import com.zara.Zara.entities.Transaction;
import com.zara.Zara.services.ITransactionService;
import com.zara.Zara.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.zara.Zara.constants.Keys.ACCOUNT_NUMBER_PREFIX;

public class BusinessNumbersGenerator {

    public static String generateAccountNumber(IUserService userService){
        String accountNumber = ACCOUNT_NUMBER_PREFIX+String.valueOf(GenerateRandomStuff.getRandomNumber(1000000));
        AppUser appUser = userService.findByAccountNumber(accountNumber);
        if(appUser==null){
            return accountNumber;

        }
        else{
            generateAccountNumber(userService);
        }
        return null;
    }

    public static String generateAgentNumber(IUserService userService){
        String agentNumber = ACCOUNT_NUMBER_PREFIX+String.valueOf(GenerateRandomStuff.getRandomNumber(10000));
        AppUser appUser = userService.findByAgentNumber(agentNumber);
        if(appUser==null){
            return agentNumber;

        }
        else{
            generateAgentNumber(userService);
        }
        return null;
    }

    public static String generateTransationNumber(ITransactionService transactionService){
        String transactionNumber = GenerateRandomStuff.getRandomString(3).toUpperCase()
                +String.valueOf(GenerateRandomStuff.getRandomNumber(1000))
                +GenerateRandomStuff.getRandomString(2)
                +String.valueOf(GenerateRandomStuff.getRandomNumber(1000));
        Transaction transaction = transactionService.findByTransactionNumber(transactionNumber);
        if(transaction==null){
            return transactionNumber;

        }
        else{
            generateTransationNumber(transactionService);
        }
        return null;
    }

}
