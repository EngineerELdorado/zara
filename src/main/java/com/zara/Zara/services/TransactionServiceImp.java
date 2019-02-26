package com.zara.Zara.services;

import com.zara.Zara.entities.PesapayTransaction;
import com.zara.Zara.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class TransactionServiceImp implements ITransactionService {

    @Autowired
    TransactionRepository transactionRepository;
    @Override
    public PesapayTransaction addTransaction(PesapayTransaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Collection<PesapayTransaction> getAll() {
        return transactionRepository.getAll();
    }

    @Override
    public Collection<PesapayTransaction> findByCustomerId(Long id) {
        return transactionRepository.findByCustomerId(id);
    }

    @Override
    public Collection<PesapayTransaction> findCustomerEntries(Long id) {
        return transactionRepository.findCustomerEntries(id);
    }

    @Override
    public Collection<PesapayTransaction> findCustomerOuts(Long id) {
        return transactionRepository.findCustomerOuts(id);
    }

    @Override
    public PesapayTransaction findByTransactionNumber(String transactionNumber) {
        return transactionRepository.findByTransactionNumber(transactionNumber);
    }

    @Override
    public Collection<PesapayTransaction> getMiniStatement(Long id) {
        return transactionRepository.getMiniStatement(id);
    }

    @Override
    public Page<PesapayTransaction> findByBusiness(Long id, Pageable pageable) {
        return transactionRepository.findByBusiness(id, pageable);
    }

}
