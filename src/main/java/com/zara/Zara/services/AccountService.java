package com.zara.Zara.services;

import com.zara.Zara.entities.Account;
import com.zara.Zara.entities.User;
import com.zara.Zara.exceptions.exceptions.Zaka400Exception;
import com.zara.Zara.exceptions.exceptions.Zaka500Exception;
import com.zara.Zara.repositories.AccountRepository;
import com.zara.Zara.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public Account findAccountByRecipient(String recipient) {

        Account account = accountRepository.findByAccountNumberForUpdate(recipient).orElse(null);
        if (account == null) {
            User user = userRepository.findByEmail(recipient).orElse(null);
            if (user == null) {
                user = userRepository.findByPhoneNumber(recipient).orElseThrow(() -> new Zaka400Exception("" +
                        "Account not found for " + recipient));
                if (user == null) {
                    throw new Zaka500Exception(" Account not found for " + recipient);
                }
                if (!user.isOnboarded()) {
                    throw new Zaka500Exception("Account not onboarded for " + recipient);
                }

            }
            account = accountRepository.findByUserIdFofUpdate(user.getId()).orElseThrow(() -> new Zaka400Exception("" +
                    "Account not found for " + recipient));
            if (account == null) {
                throw new Zaka400Exception("Account not found for " + recipient);
            }
        }

        return account;
    }
}
