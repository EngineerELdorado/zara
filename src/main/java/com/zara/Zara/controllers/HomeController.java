package com.zara.Zara.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping("")
    public ResponseEntity<?> home(){
        return ResponseEntity.status(200).body("WELCOME TO ZARA");
    }
}
