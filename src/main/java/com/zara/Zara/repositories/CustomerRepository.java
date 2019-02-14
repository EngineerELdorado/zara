package com.zara.Zara.repositories;

import com.zara.Zara.entities.Customer;
import com.zara.Zara.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByPhoneNumber(String phoneNumber);
    @Query(value = "select balance from customers where id =?1", nativeQuery = true)
    BigDecimal getBalance(Long customerId);

    @Override
    @Query(value = "select * from customers order by id DESC", nativeQuery = true)
    List<Customer> findAll();
    @Query(value = "select * from customers order by id DESC", nativeQuery = true)
    List<Customer> findByStatus(String status);
}
