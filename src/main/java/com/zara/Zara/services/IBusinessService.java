package com.zara.Zara.services;

import com.zara.Zara.entities.Business;
import org.springframework.data.domain.Page;

import java.util.Collection;

public interface IBusinessService {
    
    Business save(Business business);
    Business findByBusinessNumber(String businessNumber);
    Business findByEmail(String email);
    Page<Business> findAll(int page, int size, Long start, Long end, String param);
    Business findOne(Long id);
    Business findByPhoneNumber(String phoneNumber);
    Long findCount(Long start, Long end);
}
