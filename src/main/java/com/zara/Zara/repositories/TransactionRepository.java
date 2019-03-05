package com.zara.Zara.repositories;

import com.zara.Zara.entities.PesapayTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigDecimal;
import java.net.ContentHandler;
import java.util.Collection;
import java.util.Date;

public interface TransactionRepository extends PagingAndSortingRepository<PesapayTransaction, Long> {

    @Query(value = "select * from transaction where transaction_number=?1", nativeQuery = true)
    PesapayTransaction findByTransactionNumber(String transationNumber);
    @Query(value = "select * from transaction where created_by_id=?1 or receiver_id=?1 order by id DESC limit 50", nativeQuery = true)
    Collection<PesapayTransaction>getMiniStatement(Long userId);
    @Query(value = "select * from transaction order by id desc ", nativeQuery = true)
    Collection<PesapayTransaction>getAll();
    @Query(value = "select * from transaction where created_by_customer_id =?1 or received_by_customer_id=?1 order by id desc", nativeQuery = true)
    Collection<PesapayTransaction> findByCustomerId(Long id);

    @Query(value = "select * from transaction where received_by_customer_id=?1 order by id desc", nativeQuery = true)
    Collection<PesapayTransaction> findCustomerEntries(Long id);

    @Query(value = "select * from transaction where created_by_customer_id=?1 order by id desc", nativeQuery = true)
    Collection<PesapayTransaction> findCustomerOuts(Long id);
    @Query(value = "select * from transaction where created_by_business_id=?1 or received_by_business_id=?1",
            countQuery = "select count(*) from transaction where created_by_business_id=?1 or received_by_business_id=?1",
            nativeQuery = true)
    Page<PesapayTransaction> findByBusiness(Long id, Pageable pageable);

    @Query(value = "select * from transaction where received_by_business_id=?1",
            countQuery = "select count(*) from transaction where received_by_business_id=?1",
            nativeQuery = true)
    Page<PesapayTransaction> findEntriesByBusiness(Long id, Pageable pageable);
    @Query(value = "select * from transaction where created_by_business_id=?1",
            countQuery = "select count(*) from transaction where created_by_business_id=?1",
            nativeQuery = true)
    Page<PesapayTransaction> findOutsByBusiness(Long id, Pageable pageable);

    @Query(value = "select count(*) from transaction where created_by_business_id=?1 or received_by_business_id=?1", nativeQuery = true)
    int countByBusiness(Long id);
    @Query(value = "select count(*) from transaction where received_by_business_id=?1", nativeQuery = true)
    int countEntriesByBusiness(Long id);
    @Query(value = "select count(*) from transaction where created_by_business_id=?1", nativeQuery = true)
    int countOutsByBusiness(Long id);

    @Query(value = "select count(*) from transaction where created_by_business_id=?1 and transaction_type='BULK_PAYMENT'", nativeQuery = true)
    int countBulkByBusiness(Long id);

    @Query(value = "select * from transaction where created_by_business_id=?1 and transaction_type='BULK_PAYMENT' " +
            "and b_type=?2",
            countQuery = "select count(*) from transaction where created_by_business_id=?1  and transaction_type='BULK_PAYMENT' and b_type=?2",
            nativeQuery = true)
    Page<PesapayTransaction> findBukByBusiness(Long id, String type, Pageable pageable);

    @Query(value = "select sum(amount) from transaction where created_by_business_id=?1 or received_by_business_id=?1 ", nativeQuery = true)
    BigDecimal allStatsSumByBusiness(Long businessId);

    @Query(value = "select sum(amount) from transaction where received_by_business_id=?1 ", nativeQuery = true)
    BigDecimal entriesStatsSumByBusiness(Long businessId);

    @Query(value = "select sum(amount) from transaction where created_by_business_id=?1 ", nativeQuery = true)
    BigDecimal outsStatsSumByBusiness(Long businessId);

    @Query(value = "select * from transaction where created_by_business_id=?1 or received_by_business_id=?1 " +
            "order by id desc limit 10", nativeQuery = true)
    Collection<PesapayTransaction> allStatsTransactionsByBusiness(Long businessId);

    @Query(value = "select * from transaction where received_by_business_id=?1 " +
            "order by id desc limit 10", nativeQuery = true)
    Collection<PesapayTransaction> entriesStatsTransactionsByBusiness(Long businessId);
    @Query(value = "select * from transaction where created_by_business_id=?1 " +
            "order by id desc limit 10", nativeQuery = true)
    Collection<PesapayTransaction> outsStatsTransactionsByBusiness(Long businessId);
    @Query(value = "select * from transaction where created_by_business_id=?1 or received_by_business_id=?1" +
            " and transaction_number like %?filter%",
            countQuery = "select count(*) from transaction where created_by_business_id=?1 or received_by_business_id=?1" +
                    " and transaction_number like %?2%",
            nativeQuery = true)
    Page<PesapayTransaction> findByBusinessWithFilter(Long id, String filter, Pageable pageable);
}
