package com.zara.Zara.services;

import com.zara.Zara.entities.Admin;

import java.util.Collection;

public interface IAdminService {

    Admin save(Admin admin);
    Admin findByUsername(String username);
    Collection<Admin>findAll();
}
