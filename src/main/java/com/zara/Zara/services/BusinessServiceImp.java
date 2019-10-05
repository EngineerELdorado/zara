package com.zara.Zara.services;

import com.zara.Zara.entities.Business;
import com.zara.Zara.repositories.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public Page<Business> findAll(int page, int size, Long start, Long end, String param) {
        Pageable pageable = PageRequest.of(page,size);
        return businessRepository.findPagedBusinesses(start,end,param,pageable);
    }

    @Override
    public Page<Business> filter(int page, int size, String param) {
        Pageable pageable = PageRequest.of(page,size);
        return businessRepository.filter(param,pageable);
    }

    @Override
    public Business findOne(Long id) {
        return businessRepository.getOne(id);
    }

    @Override
    public Business findByPhoneNumber(String phoneNumber) {
        return businessRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public Long findCount(Long start, Long end) {
        return businessRepository.findCount(start,end);
    }
}
