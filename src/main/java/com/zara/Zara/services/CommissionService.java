package com.zara.Zara.services;

import com.zara.Zara.dtos.requests.CommissionTransferRequest;
import com.zara.Zara.entities.Account;
import com.zara.Zara.entities.CommissionAccount;
import com.zara.Zara.exceptions.exceptions.Zaka400Exception;
import com.zara.Zara.exceptions.exceptions.Zaka500Exception;
import com.zara.Zara.repositories.AccountRepository;
import com.zara.Zara.repositories.CommissionAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class CommissionService {

    private final CommissionAccountRepository commissionAccountRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public void transfer(Long accountId, CommissionTransferRequest request) {


        CommissionAccount commissionAccount = commissionAccountRepository.findByPrepaidAccountIdForUpdate(accountId)
                .orElseThrow(() -> new Zaka400Exception("Commission account not found"));

        if (commissionAccount.getBalance().compareTo(request.getAmount()) > 0) {
            throw new Zaka500Exception("Commissions not enough for this transfer. your current commission is "
                    + commissionAccount.getBalance());
        }
        commissionAccount.setBalance(commissionAccount.getBalance().subtract(request.getAmount()));
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new Zaka400Exception("Account not found"));
        account.setBalance(account.getBalance().add(request.getAmount()));

        try {
            commissionAccountRepository.save(commissionAccount);
            accountRepository.save(account);
        } catch (Exception e) {

        }
    }
}
