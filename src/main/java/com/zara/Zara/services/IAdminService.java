package com.zara.Zara.services;

import com.zara.Zara.entities.Admin;

public interface IAdminService {

    Admin save(Admin admin);
    Admin findByUsername(String username);
}
