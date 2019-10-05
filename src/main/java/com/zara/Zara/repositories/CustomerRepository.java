package com.zara.Zara.repositories;

import com.zara.Zara.entities.Customer;
import com.zara.Zara.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByPhoneNumber(String phoneNumber);
    @Query(value = "select balance from customers where id =?1", nativeQuery = true)
    BigDecimal getBalance(Long customerId);

    @Query(value = "select * from customers where creation_date between ?1 and ?2  order by id desc", nativeQuery = true)
    Page<Customer> findAllCusotomers(Long start, Long end, String param, Pageable pageable);

    List<Customer> findByStatus(String status);

    @Query(value = "select count(*) from customers where creation_date between ?1 and ?2", nativeQuery = true)
    Long findCount(Long start, Long end);
    @Query(value = "select * from customers where lower(full_name) like %?1% or phone_number like %?1% or lower (status) like %?1% order by id desc", nativeQuery = true)
    Page<Customer> filter(String param, Pageable pageable);
}
