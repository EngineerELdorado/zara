package com.zara.Zara.services;

import com.zara.Zara.entities.PesapayTransaction;
import com.zara.Zara.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.ContentHandler;
import java.util.Collection;
import java.util.Date;

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

    @Override
    public Page<PesapayTransaction> findEntriesByBusiness(Long id, Pageable pageable) {
        return transactionRepository.findEntriesByBusiness(id, pageable);
    }

    @Override
    public Page<PesapayTransaction> findOutsByBusiness(Long id, Pageable pageable) {
        return transactionRepository.findOutsByBusiness(id, pageable);
    }

    @Override
    public int countByBusiness(Long id) {
        return transactionRepository.countByBusiness(id);
    }

    @Override
    public int countEntriesByBusiness(Long id) {
        return transactionRepository.countEntriesByBusiness(id);
    }

    @Override
    public int countOutsByBusiness(Long id) {
        return transactionRepository.countOutsByBusiness(id);
    }

    @Override
    public Page<PesapayTransaction> findBulkByBusiness(Long id, String type, Pageable pageable) {
        return transactionRepository.findBukByBusiness(id, type, pageable);
    }

    @Override
    public int counBulkByBusiness(Long id) {
        return transactionRepository.countBulkByBusiness(id);
    }

    @Override
    public BigDecimal allStatsSum(Long businessId, String start, String end) {
        return transactionRepository.allStatsSum(businessId, start, end);
    }

}
