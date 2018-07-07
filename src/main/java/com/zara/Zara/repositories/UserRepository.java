package com.zara.Zara.repositories;

import com.zara.Zara.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser,String> {

    @Query(value = "select * from app_user where account_number =?1 or phone=?1 limit 1", nativeQuery = true)
    AppUser findByAccountNumber(String accountNumber);
    @Query(value = "select * from app_user where phone =?1", nativeQuery = true)
    Optional<AppUser> findByPhoneNumber(String phoneNumber);
    @Query(value = "select * from app_user where agent_number=?1", nativeQuery = true)
    Optional<AppUser>findByAgentNumber(String agentNumber);
    @Query(value = "select balance from app_user where account_number=?1 or phone=?1", nativeQuery = true)
    Double getUserBalance(String accountNumber);
}
