package com.zara.Zara.repositories;

import com.zara.Zara.entities.Business;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = "select * from businesses where creation_date between ?1 and ?2", nativeQuery = true)
    Page<Business> findPagedBusinesses(Long start, Long end, String param,Pageable pageable);

    @Query(value = "select count(*) from businesses where creation_date between ?1 and ?2", nativeQuery = true)
    Long findCount(Long start, Long end);
    @Query(value = "select * from businesses where lower (business_name) like %?1% or lower (business_number) like %?1% order by id DESC", nativeQuery = true)
    Page<Business> filter(String param, Pageable pageable);
    @Query(value = "select * from business where upper(type) =?1", nativeQuery = true)
    Business findByType(String type);
}
