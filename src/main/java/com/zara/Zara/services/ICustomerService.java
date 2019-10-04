package com.zara.Zara.services;

import com.zara.Zara.entities.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collection;

public interface ICustomerService {

    Customer save(Customer appUser);
    Customer findOne(Long id);
    Customer findByPhoneNumber(String phoneNumber);
    BigDecimal getBalance(Long  customerId);
    Page<Customer> findAll(int page, int size, Long start, Long end, String param);
    Collection<Customer>findByStatus(String status);
}
