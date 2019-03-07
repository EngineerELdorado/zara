package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Setting;
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
    public ResponseEntity<?>post(@RequestBody Setting setting){
        settingsService.save(setting);

        apiResponse.setResponseCode("00");
        apiResponse.setResponseCode("Setting Created");

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/get")
    public ResponseEntity<?>getSettings(){
        return new ResponseEntity<>(settingsService.findSettings(), HttpStatus.OK);
    }
}
