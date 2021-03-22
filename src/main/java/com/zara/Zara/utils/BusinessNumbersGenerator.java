package com.zara.Zara.utils;

import com.zara.Zara.repositories.UserRepository;

public class BusinessNumbersGenerator {

    public static String generateAccountNumber(UserRepository accountRepository) {

        String accountNumber = GenerateRandomStuff.getRandomNumber(9)
                + GenerateRandomStuff.getRandomString(5) + GenerateRandomStuff.getRandomNumber(9);
        boolean numberISTaken = accountRepository.findByPhoneNumber(accountNumber).isPresent();
        if (!numberISTaken) {
            return accountNumber;
        } else {
            generateAccountNumber(accountRepository);
        }
        return null;
    }
}
