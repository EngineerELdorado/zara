package com.zara.Zara.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/ussd")
@CrossOrigin(origins = "*")
public class UssdController {

     @PostMapping("/process")
    public String ussdRequest(@RequestParam String sessionId,
                              @RequestParam String serviceCode,
                              @RequestParam String phoneNumber,
                              @RequestParam String text) throws UnsupportedEncodingException, JsonProcessingException {


        return null;
    }

    private void processPayment(String phoneNumber,
                                String businessNumber,
                                String amount,
                                String accountNumber) throws UnsupportedEncodingException, JsonProcessingException {

    }

    public void sendSms(String name, String phone, String msg) throws UnsupportedEncodingException {

    }
}
