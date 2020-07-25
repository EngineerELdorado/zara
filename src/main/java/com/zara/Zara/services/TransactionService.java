package com.zara.Zara.services;

import com.zara.Zara.dtos.requests.TransactionRequest;
import com.zara.Zara.entities.Account;
import com.zara.Zara.entities.BalanceLog;
import com.zara.Zara.entities.Currency;
import com.zara.Zara.entities.Transaction;
import com.zara.Zara.exceptions.exceptions.Zaka400Exception;
import com.zara.Zara.exceptions.exceptions.Zaka500Exception;
import com.zara.Zara.repositories.AccountRepository;
import com.zara.Zara.repositories.BalanceLogRepository;
import com.zara.Zara.repositories.CurrencyRepository;
import com.zara.Zara.repositories.TransactionRepository;
import com.zara.Zara.utils.GenerateRandomStuff;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final CurrencyService currencyService;
    private final CurrencyRepository currencyRepository;
    private final BalanceLogRepository balanceLogRepository;

    @Transactional
    public Transaction processTransaction(TransactionRequest request, Long accountId) {

        Account senderAccount = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new Zaka400Exception("Account not found"));

        if (request.getAmount().compareTo(senderAccount.getBalance()) > 0) {

            throw new Zaka400Exception("Insufficient Balance. Your current balance is " + senderAccount.getBalance());
        }

        Account receiverAccount = accountService.findAccountByRecipient(request.getRecipient());

        BigDecimal currentSenderBalance = senderAccount.getBalance();
        BigDecimal currentReceiverBalance = receiverAccount.getBalance();
        Currency currency = currencyRepository.findByCodeIgnoreCase(senderAccount.getCurrency().getCode()).orElseThrow(
                () -> new Zaka400Exception("Currency not supported")
        );

        BigDecimal senderAmount = request.getAmount();
        BigDecimal senderAmountInUSd = currencyService.convert(senderAccount.getCurrency().getCode(),
                "USD", request.getAmount(), 2, RoundingMode.HALF_UP);
        BigDecimal senderAmountInReceiverCurrency = currencyService.convert(receiverAccount.getCurrency().getCode(),
                senderAccount.getCurrency().getCode(), request.getAmount(), 2, RoundingMode.HALF_UP);

        BigDecimal charges = senderAmount.multiply(BigDecimal.valueOf(5))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal chargesInUsd = currencyService.convert(senderAccount.getCurrency().getCode(),
                "USD", charges, 2, RoundingMode.HALF_UP);

        BigDecimal chargesInReceiverCurrency = currencyService.convert(senderAccount.getCurrency().getCode(),
                receiverAccount.getCurrency().getCode(), request.getAmount(), 2, RoundingMode.HALF_UP);


        BigDecimal receiverAmountInSenderCurrency = senderAmount.subtract(charges);
        BigDecimal receiverAmountInUsd = currencyService.convert(senderAccount.getCurrency().getCode(),
                "USD", receiverAmountInSenderCurrency, 2, RoundingMode.HALF_UP);
        BigDecimal receiverAmount = currencyService.convert(senderAccount.getCurrency().getCode(),
                receiverAccount.getCurrency().getCode(), request.getAmount(), 2, RoundingMode.HALF_UP);

        BigDecimal balanceAfterForTheSender = currentSenderBalance.subtract(senderAmount);
        BigDecimal balanceAfterForTheReceiver = currentReceiverBalance.add(receiverAmount);

        Transaction transaction = new Transaction();
        transaction.setType(request.getTransactionType());
        transaction.setSenderAmount(senderAmount);
        transaction.setSenderAmountInUsd(senderAmountInUSd);
        transaction.setSenderAmountInReceiverCurrency(senderAmountInReceiverCurrency);

        transaction.setChargesInSenderCurrency(charges);
        transaction.setChargesInUsd(chargesInUsd);
        transaction.setChargesInReceiverCurrency(chargesInReceiverCurrency);

        transaction.setReceiverAmount(receiverAmount);
        transaction.setReceiverAmountInUsd(receiverAmountInUsd);
        transaction.setReceiverAmountInSenderCurrency(receiverAmountInSenderCurrency);

        transaction.setFxRate(currency.getRate());

        transaction.setSenderCurrency(senderAccount.getCurrency());
        transaction.setReceiverCurrency(receiverAccount.getCurrency());
        transaction.setTransactionNumber(GenerateRandomStuff.getRandomString(10));
        transaction.setSenderAccount(senderAccount);
        transaction.setReceiverAccount(receiverAccount);

        try {
            transaction = transactionRepository.save(transaction);
        } catch (Exception e) {
            log.error("Transaction failed. Possible cause: " + e.getCause());
            throw new Zaka500Exception("Operation failed. Please try again or contact support");
        }

        BalanceLog balanceLogForTheSender = new BalanceLog();
        balanceLogForTheSender.setAccount(senderAccount);
        balanceLogForTheSender.setTransaction(transaction);
        balanceLogForTheSender.setBalanceBefore(currentSenderBalance);
        balanceLogForTheSender.setBalanceAfter(balanceAfterForTheSender);

        try {
            balanceLogRepository.save(balanceLogForTheSender);
        } catch (Exception e) {
            log.error("Failed to save balance log. Possible cause: " + e.getCause());
            throw new Zaka500Exception("Operation failed. please try again or contact support");
        }

        BalanceLog balanceLogForTheReceiver = new BalanceLog();
        balanceLogForTheReceiver.setAccount(receiverAccount);
        balanceLogForTheReceiver.setTransaction(transaction);
        balanceLogForTheReceiver.setBalanceBefore(currentReceiverBalance);
        balanceLogForTheReceiver.setBalanceAfter(balanceAfterForTheReceiver);

        try {
            balanceLogRepository.save(balanceLogForTheReceiver);
        } catch (Exception e) {
            log.error("Failed to save balance log. Possible cause: " + e.getCause());
            throw new Zaka500Exception("Operation failed. please try again or contact support");
        }

        senderAccount.setBalance(balanceAfterForTheSender);
        try {
            accountRepository.save(senderAccount);
        } catch (Exception e) {
            log.error("Failed to save balance for senderAccount: " + e.getCause());
            throw new Zaka500Exception("Operation failed. please try again or contact support");
        }

        receiverAccount.setBalance(balanceAfterForTheReceiver);
        try {
            accountRepository.save(senderAccount);
        } catch (Exception e) {
            log.error("Failed to save balance for receiverAccount: " + e.getCause());
            throw new Zaka500Exception("Operation failed. please try again or contact support");
        }

        return transaction;
    }
}
