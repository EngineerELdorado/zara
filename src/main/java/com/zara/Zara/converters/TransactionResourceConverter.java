package com.zara.Zara.converters;

import com.zara.Zara.dtos.responses.AccountResponse;
import com.zara.Zara.dtos.responses.TransactionResponse;
import com.zara.Zara.entities.Account;
import com.zara.Zara.entities.BalanceLog;
import com.zara.Zara.entities.Transaction;
import com.zara.Zara.enums.TransactionType;
import com.zara.Zara.exceptions.exceptions.Zaka500Exception;
import com.zara.Zara.repositories.BalanceLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionResourceConverter {

    private final BalanceLogRepository balanceLogRepository;

    public TransactionResponse convert(Transaction transaction) {

        BalanceLog balanceLog = balanceLogRepository.findByTransactionIdAndAccountId(
                transaction.getId(), transaction.getSenderAccount().getId())
                .orElseThrow(() -> new Zaka500Exception("Something went wrong... please try again or contact support"));
        Account receiverAccount = transaction.getReceiverAccount();
        Account senderAccount = transaction.getSenderAccount();
        AccountResponse recipient;
        if (TransactionType.isThirdPartyTransaction(transaction.getType())) {
            recipient = new AccountResponse();
            recipient.setContact(transaction.getThirdPartyRecipient());
        } else {
            recipient = AccountResponse.builder()
                    .accountHolder(receiverAccount.getUser()
                            .getFirstName() + " " + receiverAccount.getUser().getLastName())
                    .accountId(receiverAccount.getId())
                    .accountNumber(receiverAccount.getAccountNumber())
                    .email(receiverAccount.getUser().getEmail())
                    .phone(receiverAccount.getUser().getPhoneNumber())
                    .accountType(receiverAccount.getType().name())
                    .build();
        }
        return TransactionResponse.builder()
                .transactionId(transaction.getId())
                .transactionNumber(transaction.getTransactionNumber())
                .createdAt(transaction.getCreatedAt())
                .type(transaction.getType().name())
                .status(transaction.getTransactionStatus().name())
                .sender(AccountResponse.builder()
                        .accountHolder(senderAccount.getUser()
                                .getFirstName() + " " + senderAccount.getUser().getLastName())
                        .accountId(senderAccount.getId())
                        .accountNumber(senderAccount.getAccountNumber())
                        .email(senderAccount.getUser().getEmail())
                        .phone(senderAccount.getUser().getPhoneNumber())
                        .accountType(senderAccount.getType().name())
                        .build())
                .recipient(recipient)
                .exchangeRate(transaction.getFxRate())
                .senderAmount(transaction.getSenderAmount())
                .senderAmountInUsd(transaction.getSenderAmountInUsd())
                .senderAmountInReceiverCurrency(transaction.getSenderAmountInReceiverCurrency())
                .chargesInSenderCurrency(transaction.getChargesInSenderCurrency())
                .chargesInUsd(transaction.getChargesInUsd())
                .chargesInReceiverCurrency(transaction.getChargesInReceiverCurrency())
                .commissionForPesaPayInSenderCurrency(transaction.getCommissionForPesaPayInSenderCurrency())
                .commissionForPesaPayInUSd(transaction.getCommissionForPesaPayInUSd())
                .commissionForPesaPayInReceiverCurrency(transaction.getCommissionForPesaPayInReceiverCurrency())
                .commissionForSenderInSenderCurrency(transaction.getCommissionForSenderInSenderCurrency())
                .commissionForSenderInUSd(transaction.getCommissionForSenderInUSd())
                .commissionForSenderInReceiverCurrency(transaction.getCommissionForPesaPayInReceiverCurrency())
                .commissionForReceiverInSenderCurrency(transaction.getCommissionForReceiverInSenderCurrency())
                .commissionForReceiverInUSd(transaction.getCommissionForReceiverInUSd())
                .commissionForReceiverInReceiverCurrency(transaction.getCommissionForReceiverInReceiverCurrency())
                .receiverAmount(transaction.getReceiverAmount())
                .receiverAmountInUsd(transaction.getReceiverAmountInUsd())
                .senderAmountInReceiverCurrency(transaction.getSenderAmountInReceiverCurrency())
                .senderCurrency(transaction.getSenderCurrency().getCode())
                .receiverCurrency(transaction.getReceiverCurrency().getCode())
                .balanceBefore(balanceLog.getBalanceBefore())
                .balanceAfter(balanceLog.getBalanceAfter())
                .exchangeRate(transaction.getFxRate())
                .receiverAmountInSenderCurrency(transaction.getReceiverAmountInSenderCurrency())
                .build();
    }
}
