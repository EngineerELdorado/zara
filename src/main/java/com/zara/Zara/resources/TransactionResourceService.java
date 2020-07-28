package com.zara.Zara.resources;

import com.zara.Zara.converters.TransactionResourceConverter;
import com.zara.Zara.dtos.requests.TransactionRequest;
import com.zara.Zara.dtos.responses.TransactionResponse;
import com.zara.Zara.entities.Transaction;
import com.zara.Zara.enums.TransactionType;
import com.zara.Zara.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TransactionResourceService {

    private final TransactionResourceConverter transactionResourceConverter;
    private final TransactionService transactionService;

    @Transactional
    public TransactionResponse transfer(TransactionRequest request, Long accountId) {
        if (TransactionType.isThirdPartyTransaction(request.getTransactionType())) {
            return transactionResourceConverter.convert(transactionService.processPesaPayThirdPartyTransaction(request, accountId));
        }
        return transactionResourceConverter.convert(transactionService.processPesaPayDirectTransaction(request, accountId));
    }

    public Page<TransactionResponse> history(int page, int size, LocalDateTime startDate, LocalDateTime endDate) {
        Page<Transaction> transactions = transactionService.history(page, size, startDate, endDate);
        return transactions.map(transactionResourceConverter::convert);
    }

    public Page<TransactionResponse> historyByAccountId(Long accountId, int page, int size, LocalDateTime startDate, LocalDateTime endDate) {
        Page<Transaction> transactions = transactionService.historyByAccountId(accountId, page, size, startDate, endDate);
        return transactions.map(transactionResourceConverter::convert);
    }

    public void approve(Long transactionId) {
        transactionService.approve(transactionId);
    }

    public void reject(Long transactionId) {
        transactionService.reject(transactionId);
    }
}
