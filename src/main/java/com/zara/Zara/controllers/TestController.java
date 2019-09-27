package com.zara.Zara.controllers;

import com.zara.Zara.utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@RequestMapping("/tests")
public class TestController {

    @Autowired
    EmailService emailService;

    @PostMapping("/sendEmail")
    public ResponseEntity<?>testSendingEmail(@RequestParam String subject,@RequestParam String mail,@RequestParam String email) throws IOException, MessagingException {

        emailService.sendmail(subject, mail, email);
        return new ResponseEntity<>("EMAIL SENT", HttpStatus.OK);
    }
}
