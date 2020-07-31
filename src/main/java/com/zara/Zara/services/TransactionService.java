package com.zara.Zara.services;

import com.zara.Zara.dtos.requests.TransactionRequest;
import com.zara.Zara.entities.*;
import com.zara.Zara.enums.AccountType;
import com.zara.Zara.enums.TransactionStatus;
import com.zara.Zara.enums.TransactionType;
import com.zara.Zara.exceptions.exceptions.Zaka400Exception;
import com.zara.Zara.exceptions.exceptions.Zaka500Exception;
import com.zara.Zara.repositories.*;
import com.zara.Zara.utils.GenerateRandomStuff;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

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
    private final CommissionAccountRepository commissionAccountRepository;

    @Transactional
    public Transaction processPesaPayDirectTransaction(TransactionRequest request, Long accountId) {

        CommissionAccount pesapayCommissionAccount = commissionAccountRepository
                .findMainCommissionAccountForUpdate(true)
                .orElseThrow(() -> new Zaka400Exception("Commission Account not found"));

        Account senderAccount = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new Zaka400Exception("Account not found"));

        Account receiverAccount = accountService.findAccountByRecipient(request.getRecipient());

        CommissionAccount agentCommissionAccount;

        BigDecimal currentSenderBalance = senderAccount.getBalance();
        BigDecimal currentReceiverBalance = receiverAccount.getBalance();

        Currency currency = currencyRepository.findByCodeIgnoreCase(senderAccount.getCurrency().getCode()).orElseThrow(
                () -> new Zaka400Exception("Currency not supported"));

        BigDecimal senderAmount = request.getAmount();
        BigDecimal senderAmountInUSd = currencyService.convert(senderAccount.getCurrency().getCode(),
                "USD", request.getAmount(), 2, RoundingMode.HALF_UP);
        BigDecimal senderAmountInReceiverCurrency = currencyService.convert(senderAccount.getCurrency().getCode(),
                receiverAccount.getCurrency().getCode(), request.getAmount(), 2, RoundingMode.HALF_UP);

        BigDecimal charges = senderAmount.multiply(BigDecimal.valueOf(5))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal commissionForPesaPay;
        BigDecimal commissionForAgent;


        if (request.getAmount().add(charges).compareTo(senderAccount.getBalance()) > 0) {

            throw new Zaka400Exception("Insufficient Balance. Your current balance is " + senderAccount.getBalance() + "" +
                    " you needs to also pay the transfer fees: " + charges);
        }

        BigDecimal chargesInUsd = currencyService.convert(senderAccount.getCurrency().getCode(),
                "USD", charges, 2, RoundingMode.HALF_UP);

        BigDecimal chargesInReceiverCurrency = currencyService.convert(senderAccount.getCurrency().getCode(),
                receiverAccount.getCurrency().getCode(), charges, 2, RoundingMode.HALF_UP);

        if (request.getTransactionType().name().equals(TransactionType.WITHDRAWAL.name())) {

            if (!receiverAccount.getType().name().equals(AccountType.AGENT.name())) {
                throw new Zaka400Exception("Recipient account not an agent");
            }
            agentCommissionAccount = commissionAccountRepository
                    .findByPrepaidAccountIdForUpdate(receiverAccount.getId())
                    .orElseThrow(() -> new Zaka500Exception("Agent commission account not found"));

            commissionForAgent = chargesInReceiverCurrency.multiply(BigDecimal.valueOf(10).divide(BigDecimal.valueOf(100),
                    2, RoundingMode.HALF_UP));

            commissionForPesaPay = charges.multiply(BigDecimal.valueOf(90).divide(BigDecimal.valueOf(100),
                    2, RoundingMode.HALF_UP));

            agentCommissionAccount.setBalance(agentCommissionAccount.getBalance().add(commissionForAgent));

            try {
                commissionAccountRepository.save(agentCommissionAccount);

            } catch (Exception e) {
                log.error("Failed to save commission for Agent: " + e.getCause());
                throw new Zaka500Exception("Operation failed. please try again or contact support");
            }
        }
        else{
            commissionForPesaPay = charges;
        }
        pesapayCommissionAccount.setBalance(pesapayCommissionAccount.getBalance().add(commissionForPesaPay));

        BigDecimal receiverAmountInUsd = currencyService.convert(senderAccount.getCurrency().getCode(),
                "USD", senderAmount, 2, RoundingMode.HALF_UP);

        BigDecimal balanceAfterForTheSender = currentSenderBalance.subtract(senderAmount.add(charges));
        BigDecimal balanceAfterForTheReceiver = currentReceiverBalance.add(senderAmountInReceiverCurrency);

        Transaction transaction = new Transaction();
        transaction.setType(request.getTransactionType());
        transaction.setSenderAmount(senderAmount);
        transaction.setSenderAmountInUsd(senderAmountInUSd);
        transaction.setSenderAmountInReceiverCurrency(senderAmountInReceiverCurrency);

        transaction.setChargesInSenderCurrency(charges);
        transaction.setChargesInUsd(chargesInUsd);
        transaction.setChargesInReceiverCurrency(chargesInReceiverCurrency);

        transaction.setReceiverAmount(senderAmountInReceiverCurrency);
        transaction.setReceiverAmountInUsd(receiverAmountInUsd);
        transaction.setReceiverAmountInSenderCurrency(senderAmount);

        transaction.setFxRate(currency.getRate());

        transaction.setSenderCurrency(senderAccount.getCurrency());
        transaction.setReceiverCurrency(receiverAccount.getCurrency());
        transaction.setTransactionNumber(GenerateRandomStuff.getRandomString(10));
        transaction.setSenderAccount(senderAccount);
        transaction.setReceiverAccount(receiverAccount);
        transaction.setTransactionStatus(TransactionStatus.SUCCESS);


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


        try {
            commissionAccountRepository.save(pesapayCommissionAccount);

        } catch (Exception e) {
            log.error("Failed to save commission for PesaPay: " + e.getCause());
            throw new Zaka500Exception("Operation failed. please try again or contact support");
        }


        return transaction;
    }

    @Transactional
    public Transaction processPesaPayThirdPartyTransaction(TransactionRequest request, Long accountId) {

        Account pesaPayAccount = accountRepository.findByMainAccount(true)
                .orElseThrow(() -> new Zaka400Exception("Main Account not found"));

        Account senderAccount = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new Zaka400Exception("Account not found"));

        Account receiverAccount = accountRepository.findByMainAccount(true)
                .orElseThrow(() -> new Zaka400Exception("Main Account not found"));

        BigDecimal currentSenderBalance = senderAccount.getBalance();
        BigDecimal currentReceiverBalance = receiverAccount.getBalance();
        BigDecimal currentPesaPayBalance = pesaPayAccount.getBalance();

        Currency currency = currencyRepository.findByCodeIgnoreCase(senderAccount.getCurrency().getCode()).orElseThrow(
                () -> new Zaka400Exception("Currency not supported")
        );

        BigDecimal senderAmount = request.getAmount();
        BigDecimal senderAmountInUSd = currencyService.convert(senderAccount.getCurrency().getCode(),
                "USD", request.getAmount(), 2, RoundingMode.HALF_UP);
        BigDecimal senderAmountInReceiverCurrency = currencyService.convert(senderAccount.getCurrency().getCode(),
                receiverAccount.getCurrency().getCode(), request.getAmount(), 2, RoundingMode.HALF_UP);

        BigDecimal charges = senderAmount.multiply(BigDecimal.valueOf(5))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        if (request.getAmount().add(charges).compareTo(senderAccount.getBalance()) > 0) {

            throw new Zaka400Exception("Insufficient Balance. Your current balance is " + senderAccount.getBalance() + "" +
                    " you needs to also pay the transfer fees: " + charges);
        }

        BigDecimal chargesInUsd = currencyService.convert(senderAccount.getCurrency().getCode(),
                "USD", charges, 2, RoundingMode.HALF_UP);

        BigDecimal chargesInReceiverCurrency = currencyService.convert(senderAccount.getCurrency().getCode(),
                receiverAccount.getCurrency().getCode(), charges, 2, RoundingMode.HALF_UP);


        BigDecimal receiverAmountInUsd = currencyService.convert(senderAccount.getCurrency().getCode(),
                "USD", senderAmount, 2, RoundingMode.HALF_UP);

        BigDecimal balanceAfterForTheSender = currentSenderBalance.subtract(senderAmount.add(charges));
        BigDecimal balanceAfterForTheReceiver = currentReceiverBalance.add(senderAmountInReceiverCurrency);
        BigDecimal balanceAfterForPesaPay = currentPesaPayBalance.add(chargesInUsd);

        Transaction transaction = new Transaction();
        transaction.setType(request.getTransactionType());
        transaction.setSenderAmount(senderAmount);
        transaction.setSenderAmountInUsd(senderAmountInUSd);
        transaction.setSenderAmountInReceiverCurrency(senderAmountInReceiverCurrency);

        transaction.setChargesInSenderCurrency(charges);
        transaction.setChargesInUsd(chargesInUsd);
        transaction.setChargesInReceiverCurrency(chargesInReceiverCurrency);

        transaction.setReceiverAmount(senderAmountInReceiverCurrency);
        transaction.setReceiverAmountInUsd(receiverAmountInUsd);
        transaction.setReceiverAmountInSenderCurrency(senderAmount);

        transaction.setFxRate(currency.getRate());

        transaction.setSenderCurrency(senderAccount.getCurrency());
        transaction.setReceiverCurrency(receiverAccount.getCurrency());
        transaction.setTransactionNumber(GenerateRandomStuff.getRandomString(10));
        transaction.setSenderAccount(senderAccount);
        transaction.setReceiverAccount(receiverAccount);
        transaction.setThirdPartyRecipient(request.getRecipient());

        if (TransactionType.isThirdPartyTransaction(request.getTransactionType())) {
            transaction.setTransactionStatus(TransactionStatus.PENDING);
        }

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

        BalanceLog balanceLogForPesapay = new BalanceLog();
        balanceLogForPesapay.setAccount(pesaPayAccount);
        balanceLogForPesapay.setTransaction(transaction);
        balanceLogForPesapay.setBalanceBefore(currentPesaPayBalance);
        balanceLogForPesapay.setBalanceAfter(balanceAfterForPesaPay);

        try {
            balanceLogRepository.save(balanceLogForPesapay);
        } catch (Exception e) {
            log.error("Failed to save balance log. Possible cause: " + e.getCause());
            throw new Zaka500Exception("Operation failed. please try again or contact support");
        }

        pesaPayAccount.setBalance(balanceAfterForPesaPay);
        try {
            accountRepository.save(pesaPayAccount);
        } catch (Exception e) {
            log.error("Failed to save balance for senderAccount: " + e.getCause());
            throw new Zaka500Exception("Operation failed. please try again or contact support");
        }

        return transaction;
    }

    public Page<Transaction> history(int page, int size, LocalDateTime startDate, LocalDateTime endDate) {

        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.history(startDate, endDate, pageable);
    }

    public Page<Transaction> historyByAccountId(Long accountId, int page, int size, LocalDateTime startDate, LocalDateTime endDate) {

        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.historyByAccountId(accountId, startDate, endDate, pageable);
    }

    @Transactional
    public void approve(Long transactionId) {

        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new Zaka400Exception("Transaction not found"));
        transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        if (!transaction.getType().name().equals(TransactionStatus.PENDING.name())) {
            throw new Zaka500Exception("This transaction is not pending... and cannot be approved");
        }
        try {
            transactionRepository.save(transaction);
        } catch (Exception e) {
            log.error("Failed to approve transaction. Possible cause: " + e.getCause());
            throw new Zaka500Exception("Operation failed. please try again or contact support");
        }
    }

    @Transactional
    public void reject(Long transactionId) {

        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(() -> new Zaka400Exception("Transaction not found"));
        transaction.setTransactionStatus(TransactionStatus.REJECTED);
        Account senderAccount = transaction.getSenderAccount();
        Account receiverAccount = transaction.getReceiverAccount();
        BigDecimal currentSenderBalance = senderAccount.getBalance();
        BigDecimal currentReceiverBalance = receiverAccount.getBalance();

        BigDecimal newSenderBalance = currentSenderBalance.add(transaction.getSenderAmount());
        BigDecimal newReceiverBalance = currentReceiverBalance.subtract(transaction.getReceiverAmount());
        senderAccount.setBalance(newSenderBalance);
        receiverAccount.setBalance(newReceiverBalance);
        if (!transaction.getType().name().equals(TransactionStatus.PENDING.name())) {
            throw new Zaka500Exception("This transaction is not pending... and cannot be approved");
        }

        try {
            transactionRepository.save(transaction);
        } catch (Exception e) {
            log.error("Failed to approve transaction. Possible cause: " + e.getCause());
            throw new Zaka500Exception("Operation failed. please try again or contact support");
        }

        try {
            accountRepository.save(senderAccount);
            accountRepository.save(receiverAccount);
        } catch (Exception e) {
            log.error("Failed to approve transaction. Possible cause: " + e.getCause());
            throw new Zaka500Exception("Operation failed. please try again or contact support");
        }
    }
}
