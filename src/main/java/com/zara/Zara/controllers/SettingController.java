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
}
