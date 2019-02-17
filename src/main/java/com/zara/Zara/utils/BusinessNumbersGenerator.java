package com.zara.Zara.utils;

import com.zara.Zara.entities.Agent;
import com.zara.Zara.entities.AppUser;
import com.zara.Zara.entities.PesapayTransaction;
import com.zara.Zara.services.IAgentService;
import com.zara.Zara.services.ITransactionService;
import com.zara.Zara.services.IUserService;

import java.util.Random;

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

    public static String generateAgentNumber(IAgentService agentService){
        String agentNumber = String.format("%06d", new Random().nextInt(10000));
        Agent agent = agentService.findByAgentNumber(agentNumber);
        if(agent==null){
            return agentNumber;

        }
        else{
            generateAgentNumber(agentService);
        }
        return null;
    }

    public static String generateTransationNumber(ITransactionService transactionService){
        String transactionNumber = GenerateRandomStuff.getRandomString(3).toUpperCase()
                +String.valueOf(GenerateRandomStuff.getRandomNumber(1000))
                +GenerateRandomStuff.getRandomString(2)
                +String.valueOf(GenerateRandomStuff.getRandomNumber(1000));
        PesapayTransaction transaction = transactionService.findByTransactionNumber(transactionNumber);
        if(transaction==null){
            return transactionNumber;

        }
        else{
            generateTransationNumber(transactionService);
        }
        return null;
    }

}
