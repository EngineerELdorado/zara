package com.zara.Zara.repositories;

import com.zara.Zara.entities.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BusinessRepository extends JpaRepository<Business, Long> {
    
    Business findByBusinessNumber(String businessNumber);

    @Override
    @Query(value = "select * from businesses order by id desc", nativeQuery = true)
    List<Business> findAll();

    Business findByPhoneNumber(String phoneNumber);

    Business findByEmail(String email);
}
