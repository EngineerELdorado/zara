package com.zara.Zara.services;

import com.zara.Zara.models.Transaction;

import java.util.Collection;

public interface ITransactionService {

    Transaction addTransaction(Transaction transaction);
    Collection<Transaction>getAll();
    Transaction findByTransactionNumber(String transactionNumber);
    Collection<Transaction>getMiniStatement(Long id);
}
