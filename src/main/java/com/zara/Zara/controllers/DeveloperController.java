package com.zara.Zara.controllers;


import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Developer;
import com.zara.Zara.services.IDeveloperService;
import com.zara.Zara.utils.BusinessNumbersGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/developers")
@CrossOrigin(origins = "*")
public class DeveloperController {

    @Autowired
    IDeveloperService developerService;
    ApiResponse apiResponse = new ApiResponse();

    @PostMapping("/post")
    public ResponseEntity<?>post(@RequestBody Developer developer){
        developer.setApiKey(BusinessNumbersGenerator.generateApiKey(developerService));
        Developer createdDeveloper = developerService.save(developer);

        if (createdDeveloper==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Failed to create developer account");
        }else{
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("developer account created");
            apiResponse.setDeveloper(createdDeveloper);
        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
