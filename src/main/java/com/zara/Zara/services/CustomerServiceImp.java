package com.zara.Zara.services;

import com.zara.Zara.entities.Customer;
import com.zara.Zara.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
@Service
public class CustomerServiceImp implements ICustomerService {
    @Autowired
    CustomerRepository customerRepository;
    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer findOne(Long id) {
        return customerRepository.getOne(id);
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
    public Page<Customer> findAll(int page, int size, Long start, Long end, String param) {

        Pageable pageable = PageRequest.of(page,size);
        return customerRepository.findAllCusotomers(start, end, param, pageable);
    }

    @Override
    public Collection<Customer> findByStatus(String status) {
        return customerRepository.findByStatus(status);
    }


}
