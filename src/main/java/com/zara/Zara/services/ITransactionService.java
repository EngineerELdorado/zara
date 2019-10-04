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
    PesapayTransaction findOne(Long id);
    Collection<PesapayTransaction>getAll();
    Collection<PesapayTransaction>findByCustomerId(Long id);
    Collection<PesapayTransaction>findCustomerEntries(Long id);
    Collection<PesapayTransaction>findCustomerOuts(Long id);
    PesapayTransaction findByTransactionNumber(String transactionNumber);
    Collection<PesapayTransaction>getMiniStatement(Long id);
    Page<PesapayTransaction> findByBusiness(Long id, Pageable pageable);
    Page<PesapayTransaction> findByBusinessWithFilter(Long id, String filter, Pageable pageable);
    Page<PesapayTransaction> findEntriesByBusiness(Long id, Pageable pageable);
    Page<PesapayTransaction> findOutsByBusiness(Long id, Pageable pageable);
    Page<PesapayTransaction> findWithdrawalsByBusiness(Long id, Pageable pageable);
    Page<PesapayTransaction> findWithdrawalsByCustomer(Long id, Pageable pageable);
    int countByBusiness(Long id);
    int countEntriesByBusiness(Long id);

    int countOutsByBusiness(Long id);

    Page<PesapayTransaction> findBulkByBusiness(Long id, String type, Pageable pageable);

    int counBulkByBusiness(Long id);

    BigDecimal allStatsSumByBusiness(Long businessId);
    BigDecimal entriesStatsSumByBusiness(Long businessId);
    BigDecimal outsStatsSumByBusiness(Long businessId);

    Collection<PesapayTransaction> allStatsTransactionsByBusiness(Long businessId);
    Collection<PesapayTransaction> entriesStatsTransactionsByBusiness(Long businessId);
    Collection<PesapayTransaction> outsStatsTransactionsByBusiness(Long businessId);


    Page<PesapayTransaction> findByAgent(Long id, Pageable pageable);
    Page<PesapayTransaction> findByAgentWithFilter(Long id, String filter, Pageable pageable);
    Page<PesapayTransaction> findEntriesByAgent(Long id, Pageable pageable);
    Page<PesapayTransaction> findOutsByAgent(Long id, Pageable pageable);

    //ADMIN STUFF
    Page<PesapayTransaction>findAll(int page, int size, Long start, Long end, String param);
}
