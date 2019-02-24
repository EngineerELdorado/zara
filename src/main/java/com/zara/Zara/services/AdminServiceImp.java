package com.zara.Zara.services;

import com.zara.Zara.entities.Admin;
import com.zara.Zara.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AdminServiceImp implements IAdminService {

    @Autowired
    AdminRepository adminRepository;
    @Override
    public Admin save(Admin admin) {
        return adminRepository.save(admin);
    }

    @Override
    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    @Override
    public Collection<Admin> findAll() {
        return adminRepository.findAll();
    }
}
