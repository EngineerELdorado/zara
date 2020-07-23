package com.zara.Zara.utils;

import com.zara.Zara.repositories.AccountRepository;

public class BusinessNumbersGenerator {

    public static String generateAccountNumber(AccountRepository accountRepository) {

        String accountNumber = GenerateRandomStuff.getRandomNumber(9)
                + GenerateRandomStuff.getRandomString(5) + GenerateRandomStuff.getRandomNumber(9);
        boolean numberISTaken = accountRepository.findByAccountNumber(accountNumber).isPresent();
        if (!numberISTaken) {
            return accountNumber;
        } else {
            generateAccountNumber(accountRepository);
        }
        return null;
    }
}
