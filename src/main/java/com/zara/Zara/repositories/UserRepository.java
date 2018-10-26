package com.zara.Zara.repositories;

import com.zara.Zara.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser,String> {

    @Query(value = "select * from USERS order by ID DESC", nativeQuery = true)
    Collection<AppUser>getAll();

    AppUser findByUsername(String username);
}
