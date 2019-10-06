package com.zara.Zara.controllers.administration;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.CommissionSetting;
import com.zara.Zara.services.ICommissionSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admins/commission_settings")
@RestController
@CrossOrigin(origins = "*")
public class _CommissionSettingsController {

    @Autowired
    ICommissionSettingService commissionSettingService;
    ApiResponse apiResponse = new ApiResponse();

    @PostMapping("/save")
    public ResponseEntity<?>save(@RequestBody CommissionSetting commissionSetting, @RequestParam String by){

       commissionSetting.setSetBy(by);
       commissionSettingService.save(commissionSetting);
       apiResponse.setResponseCode("00");
       apiResponse.setResponseMessage("SETTING SAVED");
       return new ResponseEntity<>(apiResponse, HttpStatus.OK);

    }

    @GetMapping("/find-all")
    public ResponseEntity<?>findAll(int page, int size){
        apiResponse.setData(commissionSettingService.findAll(page,size));
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);

    }
}
