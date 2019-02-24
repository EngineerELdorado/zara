package com.zara.Zara.repositories;

import com.zara.Zara.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdminRepository extends JpaRepository<Admin,Long> {

    @Query(value = "select * from admins where email =?1", nativeQuery = true)
    Admin findByUsername(String username);
}
