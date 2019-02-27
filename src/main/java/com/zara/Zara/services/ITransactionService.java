package com.zara.Zara.services;

import com.zara.Zara.entities.PesapayTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface ITransactionService {

    PesapayTransaction addTransaction(PesapayTransaction transaction);
    Collection<PesapayTransaction>getAll();
    Collection<PesapayTransaction>findByCustomerId(Long id);
    Collection<PesapayTransaction>findCustomerEntries(Long id);
    Collection<PesapayTransaction>findCustomerOuts(Long id);
    PesapayTransaction findByTransactionNumber(String transactionNumber);
    Collection<PesapayTransaction>getMiniStatement(Long id);
    Page<PesapayTransaction> findByBusiness(Long id, Pageable pageable);
    int countByBusiness(Long id);
}
