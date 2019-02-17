package com.zara.Zara.services;

import com.zara.Zara.entities.Business;

import java.util.Collection;

public interface IBusinessService {
    
    Business save(Business business);
    Business findByBusinessNumber(String businessNumber);
    Collection<Business>findAll();

    Business findByPhoneNumber(String phoneNumber);
}
