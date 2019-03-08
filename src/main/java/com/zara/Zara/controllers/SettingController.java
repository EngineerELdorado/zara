package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.PesaPay;
import com.zara.Zara.services.ISettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/settings")
@RestController
@CrossOrigin(origins = "*")
public class SettingController {

    @Autowired
    ISettingsService settingsService;
    ApiResponse apiResponse = new ApiResponse();

    @PostMapping("/post")
    public ResponseEntity<?>post(@RequestBody PesaPay pesaPay){
        settingsService.save(pesaPay);

        apiResponse.setResponseCode("00");
        apiResponse.setResponseCode("PesaPay Created");
        settingsService.save(pesaPay);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/get")
    public ResponseEntity<?>getSettings(){
        return new ResponseEntity<>(settingsService.findSettings(), HttpStatus.OK);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?>post(@RequestBody PesaPay pesaPay, @PathVariable String id){

        PesaPay pesaPay1 = settingsService.findById(Long.parseLong(id));
        pesaPay1.setBankAccountNumber(pesaPay.getBankAccountNumber());
        pesaPay1.setBankAccountRoutingNumber(pesaPay.getBankAccountRoutingNumber());
        pesaPay1.setBankAccountSwiftCode(pesaPay.getBankAccountSwiftCode());
        pesaPay1.setBankAccountType(pesaPay.getBankAccountType());
        pesaPay1.setBankBeneficiaryName(pesaPay.getBankBeneficiaryName());
        pesaPay1.setBankName(pesaPay.getBankName());

        pesaPay1.setCreditCardCvv(pesaPay.getCreditCardCvv());
        pesaPay1.setCreditCardExpiryYear(pesaPay.getCreditCardExpiryYear());
        pesaPay1.setCreditExpiryMonth(pesaPay.getCreditExpiryMonth());
        pesaPay1.setCreditCardNumber(pesaPay.getCreditCardNumber());

        pesaPay1.setPaypalEmail(pesaPay.getPaypalEmail());

        settingsService.save(pesaPay);


        apiResponse.setResponseCode("00");
        apiResponse.setResponseCode("PesaPay Created");
        settingsService.save(pesaPay);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
