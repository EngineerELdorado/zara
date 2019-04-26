package com.zara.Zara.controllers;

import com.zara.Zara.entities.Customer;
import com.zara.Zara.models.Sms;
import com.zara.Zara.services.IBusinessService;
import com.zara.Zara.services.ICustomerService;
import com.zara.Zara.services.utils.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

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
                              @RequestParam String text) throws UnsupportedEncodingException {
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
            }else if (inputs[0].equals("1") && !inputs[0].equals("")  && inputs.length==1){
                message ="Entrer votre nom et postnom";
            }
            else if (inputs[0].equals("1") && !inputs[1].equals("")  && inputs.length==2){
                message ="Creer un pin (4 chiffres)";
            }else if (inputs[0].equals("1") && !inputs[2].equals("")  && inputs.length==3){
                customer = new Customer();
                customer.setFullName(inputs[1]);
                customer.setPhoneNumber(phoneNumber);
                customer.setPin(bCryptPasswordEncoder.encode(inputs[2]));
                customerService.save(customer);
                String sms ="Bievenu sur PesaPay. maintenant vous pouvez retirer deposer " +
                "payer en ligne transferer ainsi effectuer tout genre de payment avec votre telephone.";
                sendSms(inputs[1],phoneNumber,sms);
            }
        }

        return message;
    }

    public void sendSms(String name, String phone, String msg) throws UnsupportedEncodingException {
        Sms sms = new Sms();
        sms.setTo(phone);
        sms.setMessage("Cher "+name+" "+ msg);
        SmsService.sendSms(sms);
    }
}
