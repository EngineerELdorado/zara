package com.zara.Zara.repositories;

import com.zara.Zara.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query(value = "select * from role where id=?1", nativeQuery = true)
    Role getByRoleId(Long roleId);
    @Query(value = "select *from role where name=?1", nativeQuery = true)
    Role getByRoleName(String roleName);
}
