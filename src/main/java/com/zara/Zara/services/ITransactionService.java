package com.zara.Zara.services;

import com.zara.Zara.entities.PesapayTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.net.ContentHandler;
import java.util.Collection;
import java.util.Date;

public interface ITransactionService {

    PesapayTransaction addTransaction(PesapayTransaction transaction);
    Collection<PesapayTransaction>getAll();
    Collection<PesapayTransaction>findByCustomerId(Long id);
    Collection<PesapayTransaction>findCustomerEntries(Long id);
    Collection<PesapayTransaction>findCustomerOuts(Long id);
    PesapayTransaction findByTransactionNumber(String transactionNumber);
    Collection<PesapayTransaction>getMiniStatement(Long id);
    Page<PesapayTransaction> findByBusiness(Long id, Pageable pageable);
    Page<PesapayTransaction> findEntriesByBusiness(Long id, Pageable pageable);
    Page<PesapayTransaction> findOutsByBusiness(Long id, Pageable pageable);
    int countByBusiness(Long id);
    int countEntriesByBusiness(Long id);

    int countOutsByBusiness(Long id);

    Page<PesapayTransaction> findBulkByBusiness(Long id, String type, Pageable pageable);

    int counBulkByBusiness(Long id);

    BigDecimal allStatsSum(Long businessId, Date start, Date end);
    BigDecimal entriesStatsSum(Long businessId, Date start, Date end);
    BigDecimal outsStatsSum(Long businessId, Date start, Date end);
}
