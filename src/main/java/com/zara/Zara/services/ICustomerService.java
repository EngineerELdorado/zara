package com.zara.Zara.services;

import com.zara.Zara.entities.Customer;

import java.math.BigDecimal;
import java.util.Collection;

public interface ICustomerService {

    Customer addUser(Customer appUser);
    Customer findByPhoneNumber(String phoneNumber);
    BigDecimal getBalance(Long  customerId);
    Collection<Customer> findAll();
    Collection<Customer>findByStatus(String status);
}
