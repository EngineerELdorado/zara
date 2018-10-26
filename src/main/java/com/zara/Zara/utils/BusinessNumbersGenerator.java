package com.zara.Zara.utils;

import com.zara.Zara.entities.Transaction;
import com.zara.Zara.services.ITransactionService;

public class BusinessNumbersGenerator {



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
