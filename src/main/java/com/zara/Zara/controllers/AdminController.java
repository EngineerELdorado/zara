package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Admin;
import com.zara.Zara.models.LoginObject;
import com.zara.Zara.services.IAdminService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Date;

@RestController
@RequestMapping("/admins")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    IAdminService adminService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    ApiResponse apiResponse = new ApiResponse();
    Logger LOG = LogManager.getLogger(AgentController.class);

    @PostMapping("/post")
    public ResponseEntity<?>post(@RequestBody Admin admin){
        Collection<Admin>admins = adminService.findAll();
        admin.setCreatedOn(new Date());
        admin.setPassword(bCryptPasswordEncoder.encode(admin.getPassword()));
        if (admins.size()==0){
            admin.setType("Super Admin");
            LOG.info("HE IS FIRST ADMIN...WILL BE SET AS SUPER ADMIN");
        }
         Admin createdAdmin = adminService.save(admin);
        if (createdAdmin!=null){
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("Admin Created");
            apiResponse.setAdmin(createdAdmin);
            LOG.info("ADMIN CREATED");
        }else {
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Failed to create admin");
            LOG.info("FAILED TO CREATE ADMIN");
        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?>login(@RequestBody LoginObject loginObject){
        Admin admin = adminService.findByUsername(loginObject.getUsername());
        if (admin==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Wrong username or password");
            LOG.info("ADMIN LOGIN FAILED. WRONG USERNAME");
        }else{
            if (bCryptPasswordEncoder.matches(loginObject.getPassword(), admin.getPassword())){
                apiResponse.setResponseCode("00");
                apiResponse.setResponseMessage("Login successful");
                apiResponse.setAdmin(admin);
                LOG.info("ADMIN LOGIN SUCCESSFUL FOR "+admin.getFullName());
            }else{
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage("Wrong username or password");
                LOG.info("ADMIN LOGIN FAILED. WRONG PASSWORD");
            }
        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
