package com.zara.Zara.repositories;

import com.zara.Zara.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Admin, String> {

    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByPhoneNumber(String phoneNumber);

    Optional<Admin> findById(Long userId);
}
