package com.zara.Zara.services;

import com.zara.Zara.entities.Admin;
import com.zara.Zara.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    public Admin findOne(Long id) {
        return adminRepository.getOne(id);
    }

    @Override
    public Page<Admin> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return adminRepository.findAll(pageable);
    }

    @Override
    public Page<Admin> filter(int page, int size,String param) {
        Pageable pageable = PageRequest.of(page, size);
        return adminRepository.filter(param,pageable);
    }
}
