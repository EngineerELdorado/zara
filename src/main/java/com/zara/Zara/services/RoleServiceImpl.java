package com.zara.Zara.services;

import com.zara.Zara.models.Role;
import com.zara.Zara.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class RoleServiceImpl implements IRoleService {
    @Autowired
    RoleRepository roleRepository;
    @Override
    public Role add(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role getOne(Long id) {
        return roleRepository.getByRoleId(id);
    }

    @Override
    public Role getByName(String roleName) {
        return roleRepository.getByRoleName(roleName);
    }

    @Override
    public Collection<Role> all() {
        return roleRepository.findAll();
    }

    @Override
    public void delete(Long id) {
               roleRepository.deleteById(id);
    }
}
