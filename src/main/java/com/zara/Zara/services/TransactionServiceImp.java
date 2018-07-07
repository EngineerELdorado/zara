package com.zara.Zara.services;

import com.zara.Zara.models.Transaction;
import com.zara.Zara.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class TransactionServiceImp implements ITransactionService {

    @Autowired
    TransactionRepository transactionRepository;
    @Override
    public Transaction addTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Collection<Transaction> getAll() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction findByTransactionNumber(String transactionNumber) {
        return transactionRepository.findByTransactionNumber(transactionNumber);
    }

}
