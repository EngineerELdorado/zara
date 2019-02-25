package com.zara.Zara.controllers;

import com.zara.Zara.services.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/roles")
@RestController
@CrossOrigin(origins = "*")
public class RoleController {
    @Autowired
    IRoleService roleService;
    @GetMapping("/getAll")
    public ResponseEntity<?>getAll(){
        return ResponseEntity.status(200).body(roleService.all());
    }
}
