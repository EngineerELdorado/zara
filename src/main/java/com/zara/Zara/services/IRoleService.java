package com.zara.Zara.services;

import com.zara.Zara.entities.Role;

import java.util.Collection;

public interface IRoleService {
    Role add(Role role);
    Role getOne(Long id);
    Role getByName(String roleName);
    Collection<Role> all();
    void delete(Long id);
}
