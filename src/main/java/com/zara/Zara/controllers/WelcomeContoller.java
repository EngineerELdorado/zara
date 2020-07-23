package com.zara.Zara.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/welcome")
@CrossOrigin(origins = "*")
public class WelcomeContoller {

    @GetMapping("")
    public String welcome(){
        return "Welcome";
    }
}
