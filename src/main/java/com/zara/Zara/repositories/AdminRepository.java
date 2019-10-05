package com.zara.Zara.repositories;

import com.zara.Zara.entities.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdminRepository extends JpaRepository<Admin,Long> {

    @Query(value = "select * from admins where email =?1", nativeQuery = true)
    Admin findByUsername(String username);
    @Query(value = "select * from admins where lower (full_name) like %?1% or phone like %?1% order by id DESC", nativeQuery = true)
    Page<Admin> filter( String param, Pageable pageable);
}
