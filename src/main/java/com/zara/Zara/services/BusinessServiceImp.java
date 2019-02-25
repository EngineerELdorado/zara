package com.zara.Zara.services;

import com.zara.Zara.entities.Business;
import com.zara.Zara.repositories.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class BusinessServiceImp implements IBusinessService {

    @Autowired
    BusinessRepository businessRepository;
    @Override
    public Business save(Business business) {
        return businessRepository.save(business);
    }

    @Override
    public Business findByBusinessNumber(String businessNumber) {
        return businessRepository.findByBusinessNumber(businessNumber);
    }

    @Override
    public Business findByEmail(String email) {
        return businessRepository.findByEmail(email);
    }

    @Override
    public Collection<Business> findAll() {
        return businessRepository.findAll();
    }

    @Override
    public Business findByPhoneNumber(String phoneNumber) {
        return businessRepository.findByPhoneNumber(phoneNumber);
    }
}
