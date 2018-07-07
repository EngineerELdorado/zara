package com.zara.Zara.listeners;


import com.zara.Zara.models.Role;
import com.zara.Zara.services.IRoleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static com.zara.Zara.constants.ConstantVariables.*;


@Component
public class AppEventListener implements CommandLineRunner {

    @Autowired
    IRoleService roleService;

    Logger LOGGER = LogManager.getLogger(AppEventListener.class);

    @Override
    public void run(String... args) {
        LOGGER.info("............APP HAS STARTED...........");

       Collection<Role> roles= roleService.all();
       if(roles.size()==0){
           Role roleSuperAdmin = new Role();
           roleSuperAdmin.setName(ROLE_SUPERADMIN);
           roleService.add(roleSuperAdmin);
           LOGGER.info("............ADDED SUPER ADMIN ROLE...........");
           Role roleAdmin = new Role();
           roleAdmin.setName(ROLE_ADMIN);
           roleService.add(roleAdmin);
           LOGGER.info("............ADDED ADMIN ROLE...........");
           Role roleStaff= new Role();
           roleStaff.setName(ROLE_STAFF);
           roleService.add(roleStaff);
           LOGGER.info("............ADDED STAFF ROLE...........");

           Role roleUser= new Role();
           roleUser.setName(ROLE_USER);
           roleService.add(roleUser);
           LOGGER.info("............ADDED USER ROLE...........");

           Role roleAgent= new Role();
           roleAgent.setName(ROLE_AGENT);
           roleService.add(roleAgent);
           LOGGER.info("............ADDED USER ROLE...........");

       }
       else {
           LOGGER.info("............ROLES ALREADY EXIST...........");
       }
    }


}
