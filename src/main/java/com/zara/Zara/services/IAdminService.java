package com.zara.Zara.services;

import com.zara.Zara.entities.Admin;
import org.springframework.data.domain.Page;

import java.util.Collection;

public interface IAdminService {

    Admin save(Admin admin);
    Admin findByUsername(String username);
    Admin findOne(Long id);
    Page<Admin> findAll(int page, int size);
}
