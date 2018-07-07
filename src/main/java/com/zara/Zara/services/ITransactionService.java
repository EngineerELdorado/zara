package com.zara.Zara.services;

import com.zara.Zara.models.Transaction;

import java.util.Collection;

public interface ITransactionService {

    public Transaction addTransaction(Transaction transaction);
    public Collection<Transaction>getAll();
    public Transaction findByTransactionNumber(String transactionNumber);

}
