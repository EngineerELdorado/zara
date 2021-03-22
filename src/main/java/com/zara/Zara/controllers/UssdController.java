package com.zara.Zara.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ussd")
@CrossOrigin(origins = "*")
public class UssdController {

     @PostMapping("/process")
     public String ussdRequest(@RequestParam String sessionId,
                               @RequestParam String serviceCode,
                               @RequestParam String phoneNumber,
                               @RequestParam String text) {

         return null;
     }

    private void processPayment(String phoneNumber,
                                String businessNumber,
                                String amount,
                                String accountNumber) {

    }

    public void sendSms(String name, String phone, String msg) {

    }
}
