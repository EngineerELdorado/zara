package com.zara.Zara.services;

import com.zara.Zara.models.Role;

import java.util.Collection;

public interface IRoleService {
    public Role add(Role role);
    public Role getOne(Long id);
    public Role getByName(String roleName);
    public Collection<Role> all();
    public void delete(Long id);
}
