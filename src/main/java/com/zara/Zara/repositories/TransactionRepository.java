package com.zara.Zara.repositories;

import com.zara.Zara.entities.PesapayTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

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

    @Query(value = "select * from transaction where created_by_customer_id=?1 and " +
            "transaction_type in ('WITHDRAWAL','PAYPAL_WITHDRAWAL','C2C'," +
            "'AIRTELMONEY_WITHDRAWAL','ORANGEMONEY_WITHDRAWAL','MPESA_WITHDRAWAL') order by id desc", nativeQuery = true)
    Collection<PesapayTransaction> findCustomerOuts(Long id);

    @Query(value = "select * from transaction where created_by_business_id=?1 or received_by_business_id=?1",
            countQuery = "select count(*) from transaction where created_by_business_id=?1 or received_by_business_id=?1",
            nativeQuery = true)
    Page<PesapayTransaction> findByBusiness(Long id, Pageable pageable);

    @Query(value = "select * from transaction where received_by_business_id=?1",
            countQuery = "select count(*) from transaction where received_by_business_id=?1",
            nativeQuery = true)
    Page<PesapayTransaction> findEntriesByBusiness(Long id, Pageable pageable);
    @Query(value = "select * from transaction where created_by_business_id=?1 and transaction_type ='WITHDRAWAL'" +
            "or transaction_type ='PAYPAYL_WITHDRAWAL' or transaction_type ='AIRTELMONEY_WITHDRAWAL'" +
            "or transaction_type ='ORANGEMONEY_WITHDRAWAL'" +
            "or transaction_type ='MPESA_WITHDRAWAL' order by id desc",
            countQuery = "select count(*) from transaction where created_by_business_id=?1",
            nativeQuery = true)
    Page<PesapayTransaction> findOutsByBusiness(Long id, Pageable pageable);

    @Query(value = "select * from transaction where created_by_business_id=?1 and transaction_type ='WITHDRAWAL' order by id desc",
            countQuery = "select count(*) from transaction where created_by_business_id=?1 and transaction_type like %:keyword%",
            nativeQuery = true)
    Page<PesapayTransaction> findWithdrawalsByBusiness(Long id, Pageable pageable);

    @Query(value = "select * from transaction where created_by_customer_id=?1 and transaction_type ='WITHDRAWAL'",
            countQuery = "select count(*) from transaction where created_by_customer_id=?1 and transaction_type like %:keyword%",
            nativeQuery = true)
    Page<PesapayTransaction> findWithdrawalsByCustomer(Long id, Pageable pageable);

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

    @Query(value = "select * from transaction where created_by_business_id=?1 or received_by_business_id=?1",
            countQuery = "select count(*) from transaction where created_by_business_id=?1 or received_by_business_id=?1",
            nativeQuery = true)
    Page<PesapayTransaction> findByBusinessWithFilter(@Param("id")Long id, @Param("filter") String filter, Pageable pageable);


    @Query(value = "select * from transaction where created_by_agent_id=?1 or received_by_agent_id=?1",
            countQuery = "select count(*) from transaction where created_by_agent_id=?1 or received_by_agent_id=?1",
            nativeQuery = true)
    Page<PesapayTransaction> findByAgent(Long id, Pageable pageable);

    @Query(value = "select * from transaction where received_by_agent_id=?1",
            countQuery = "select count(*) from transaction where received_by_agent_id=?1",
            nativeQuery = true)
    Page<PesapayTransaction> findEntriesByAgent(Long id, Pageable pageable);
    @Query(value = "select * from transaction where created_by_agent_id=?1",
            countQuery = "select count(*) from transaction where created_by_agent_id=?1",
            nativeQuery = true)
    Page<PesapayTransaction> findOutsByAgent(Long id, Pageable pageable);

    @Query(value = "select * from transaction where  created_by_agent_id= :id or received_by_agent_id= :id" +
            " and transaction_number like %:filter%",
            countQuery = "select count(*) from transaction where created_by_agent_id=:id or received_by_agent_id= :id" +
                    " and transaction_number like %:filter%",
            nativeQuery = true)
    Page<PesapayTransaction> findByAgentWithFilter(@Param("id")Long id, @Param("filter") String filter, Pageable pageable);


    @Query(value = "select * from transaction where creation_date between ?1 and ?2 and lower(transaction_number) like %?3% or lower(transaction_type) like %?3% or status like %?3% order by id desc", nativeQuery = true)
    Page<PesapayTransaction> findPagedTransactions(Long start, Long end, String param, Pageable pageable);

    @Query(value = "select * from transaction where id =?1", nativeQuery = true)
    PesapayTransaction findOne(Long id);

    @Query(value = "select count(*) from transaction where creation_date between ?1 and ?2", nativeQuery = true)
    Long findCount(Long start, Long end);
    @Query(value = "select sum(charges) from transaction where creation_date between ?1 and ?2", nativeQuery = true)
    BigDecimal commissions(Long start, Long end);
    @Query(value = "select sum(original_amount) from transaction where creation_date between ?1 and ?2", nativeQuery = true)
    BigDecimal amounts(Long start, Long end);

    @Query(value = "select sum(original_amount) from transaction where creation_date between ?1 and ?2 and status='02'", nativeQuery = true)
    BigDecimal amountsPenfing(Long start, Long end);
}
