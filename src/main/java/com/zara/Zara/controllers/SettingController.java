package com.zara.Zara.controllers;

import com.zara.Zara.entities.Setting;
import com.zara.Zara.services.ISettingService;
import org.omg.PortableInterceptor.SUCCESSFUL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.zara.Zara.constants.Keys.RESPONSE_CODE;
import static com.zara.Zara.constants.Keys.RESPONSE_MESSAGE;
import static com.zara.Zara.constants.Keys.RESPONSE_SUCCESS;
import static com.zara.Zara.constants.Responses.SMS_DESABLED;
import static com.zara.Zara.constants.Responses.SMS_ENABLED;

@RestController
@RequestMapping("/settings")
public class SettingController {
    @Autowired
    ISettingService settingService;
    HttpHeaders responseHeaders = new HttpHeaders();

    @GetMapping("/toggleSms/{id}")
    public ResponseEntity<?>toggleSms(@PathVariable String id){
        Setting setting = settingService.getSettingById(Long.parseLong(id));
        if(setting.isSmsEnabled()){
            setting.setSmsEnabled(false);
            responseHeaders.set(RESPONSE_MESSAGE, SMS_DESABLED);
            settingService.add(setting);
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        }
        else{
            setting.setSmsEnabled(true);
            responseHeaders.set(RESPONSE_MESSAGE, SMS_ENABLED);
            settingService.add(setting);
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        }

    }
    @GetMapping("/all")
    public ResponseEntity<?>getAllSettings(){
        responseHeaders.set(RESPONSE_CODE, RESPONSE_SUCCESS);
        return new ResponseEntity<>(settingService.allSettings(),responseHeaders, HttpStatus.CREATED);
    }

    @GetMapping("/getOne/{id}")
    public ResponseEntity<?>getOne(@PathVariable String id){
        responseHeaders.set(RESPONSE_CODE, RESPONSE_SUCCESS);
        return new ResponseEntity<>(settingService.getSettingById(Long.parseLong(id)),responseHeaders, HttpStatus.CREATED);
    }
}
