package com.zara.Zara.services;

import com.zara.Zara.entities.Transaction;
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
        return transactionRepository.getAll();
    }

    @Override
    public Transaction findByTransactionNumber(String transactionNumber) {
        return transactionRepository.findByTransactionNumber(transactionNumber);
    }

    @Override
    public Collection<Transaction> getMiniStatement(Long id) {
        return transactionRepository.getMiniStatement(id);
    }

}
