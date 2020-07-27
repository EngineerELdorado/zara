package com.zara.Zara.resources;

import com.zara.Zara.converters.TransactionResourceConverter;
import com.zara.Zara.dtos.requests.TransactionRequest;
import com.zara.Zara.dtos.responses.TransactionResponse;
import com.zara.Zara.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor
public class TransactionResourceService {

    private final TransactionResourceConverter transactionResourceConverter;
    private final TransactionService transactionService;

    @Transactional
    public TransactionResponse transfer(TransactionRequest request, Long accountId) {
        return transactionResourceConverter.convert(transactionService.processTransaction(request, accountId));
    }
}
