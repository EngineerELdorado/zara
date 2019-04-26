package com.zara.Zara.controllers;

import com.zara.Zara.entities.Customer;
import com.zara.Zara.services.IBusinessService;
import com.zara.Zara.services.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ussd")
@CrossOrigin(origins = "*")
public class UssdController {

     @Autowired
     ICustomerService customerService;
     @Autowired
     IBusinessService businessService;
     @Autowired
     BCryptPasswordEncoder bCryptPasswordEncoder;

     @PostMapping("/process")
    public String ussdRequest(@RequestParam String sessionId,
                              @RequestParam String serviceCode,
                              @RequestParam String phoneNumber,
                              @RequestParam String text){
        String message="";
        String inputs[]=text.split("\\*");
        Customer customer= customerService.findByPhoneNumber(phoneNumber);
        if (customer==null){
            if (text.equals("") || text.length()==0){

                message = "Bienvenu sur PesaPay. veillez choisir une option\n\n" +
                        "1. Creer un compte\n" +
                        "2. Voir nos tarifications";
            }else if (text.equals("1")){
                // TODO: 26/04/2019 customer  registration
                message ="CON entrez votre nom et postnom";
            }else if (text.equals("2")){
                message ="END Nos tarifications:\n\n" +
                        "10$---100$: 1$\n" +
                        "100$---200$: 2$\n" +
                        "200$---300$: 3$\n" +
                        "300$---400$: 4$\n" +
                        "500---600$: 5$";
            }
        }

        return message;
    }
}
