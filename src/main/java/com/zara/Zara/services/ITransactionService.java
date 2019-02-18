package com.zara.Zara.services;

import com.zara.Zara.entities.PesapayTransaction;

import java.util.Collection;

public interface ITransactionService {

    PesapayTransaction addTransaction(PesapayTransaction transaction);
    Collection<PesapayTransaction>getAll();
    Collection<PesapayTransaction>findByCustomerPhoneNumber(String phoneNumber);
    PesapayTransaction findByTransactionNumber(String transactionNumber);
    Collection<PesapayTransaction>getMiniStatement(Long id);
}
