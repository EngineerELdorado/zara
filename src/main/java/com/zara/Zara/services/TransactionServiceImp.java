package com.zara.Zara.services;

import com.zara.Zara.entities.PesapayTransaction;
import com.zara.Zara.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    public PesapayTransaction findOne(Long id) {
        return transactionRepository.findOne(id);
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
    public Page<PesapayTransaction> findByBusinessWithFilter(Long id, String filter, Pageable pageable) {
        return transactionRepository.findByBusinessWithFilter(id, filter,pageable);
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
    public Page<PesapayTransaction> findWithdrawalsByBusiness(Long id, Pageable pageable) {
        return transactionRepository.findWithdrawalsByBusiness(id, pageable);
    }

    @Override
    public Page<PesapayTransaction> findWithdrawalsByCustomer(Long id, Pageable pageable) {
        return transactionRepository.findWithdrawalsByCustomer(id,pageable);
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
    public BigDecimal allStatsSumByBusiness(Long businessId) {
        return transactionRepository.allStatsSumByBusiness(businessId);
    }

    @Override
    public BigDecimal entriesStatsSumByBusiness(Long businessId) {
        return transactionRepository.entriesStatsSumByBusiness(businessId);
    }

    @Override
    public BigDecimal outsStatsSumByBusiness(Long businessId) {
        return transactionRepository.outsStatsSumByBusiness(businessId);
    }

    @Override
    public Collection<PesapayTransaction> allStatsTransactionsByBusiness(Long businessId) {
        return transactionRepository.allStatsTransactionsByBusiness(businessId);
    }

    @Override
    public Collection<PesapayTransaction> entriesStatsTransactionsByBusiness(Long businessId) {
        return transactionRepository.entriesStatsTransactionsByBusiness(businessId);
    }

    @Override
    public Collection<PesapayTransaction> outsStatsTransactionsByBusiness(Long businessId) {
        return transactionRepository.outsStatsTransactionsByBusiness(businessId);
    }

    @Override
    public Page<PesapayTransaction> findByAgent(Long id, Pageable pageable) {
        return transactionRepository.findByAgent(id,pageable);
    }

    @Override
    public Page<PesapayTransaction> findByAgentWithFilter(Long id, String filter, Pageable pageable) {
        return transactionRepository.findByAgentWithFilter(id,filter,pageable);
    }

    @Override
    public Page<PesapayTransaction> findEntriesByAgent(Long id, Pageable pageable) {
        return transactionRepository.findEntriesByAgent(id,pageable);
    }

    @Override
    public Page<PesapayTransaction> findOutsByAgent(Long id, Pageable pageable) {
        return transactionRepository.findOutsByAgent(id,pageable);
    }

    @Override
    public Page<PesapayTransaction> findAll(int page, int size, Long start, Long end, String param) {
        Pageable pageable = PageRequest.of(page,size);

        return transactionRepository.findPagedTransactions(start,end,param, pageable);
    }

    @Override
    public Long findCount(Long start, Long end) {
        return transactionRepository.findCount(start,end);
    }

    @Override
    public BigDecimal amounts(Long start, Long end) {
        return transactionRepository.amounts(start,end);
    }

    @Override
    public BigDecimal amountsPending(Long start, Long end) {
        return transactionRepository.amountsPenfing(start,end);
    }

    @Override
    public BigDecimal commissions(Long start, Long end) {
        return transactionRepository.commissions(start,end);
    }
}
