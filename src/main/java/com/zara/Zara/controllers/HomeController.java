package com.zara.Zara.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.zara.Zara.constants.Keys.RESPONSE_MESSAGE;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class HomeController {

    @GetMapping("")
    public ResponseEntity<?> home(){

        return ResponseEntity.status(302).header(RESPONSE_MESSAGE,"welcome").body("WELCOME TO PESAPAY");
    }
}
