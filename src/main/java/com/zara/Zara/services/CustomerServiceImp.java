package com.zara.Zara.services;

import com.zara.Zara.entities.Customer;
import com.zara.Zara.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
@Service
public class CustomerServiceImp implements ICustomerService {
    @Autowired
    CustomerRepository customerRepository;
    @Override
    public Customer addUser(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer findByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public BigDecimal getBalance(Long  customerId) {
        return customerRepository.getBalance(customerId);
    }

    @Override
    public Collection<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Collection<Customer> findByStatus(String status) {
        return customerRepository.findByStatus(status);
    }


}
