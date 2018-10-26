package com.zara.Zara.repositories;

import com.zara.Zara.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query(value = "select * from ROLES where ID=?1", nativeQuery = true)
    Role getByRoleId(Long roleId);
    @Query(value = "select *from ROLES where NAME=?1", nativeQuery = true)
    Role getByRoleName(String roleName);
}
