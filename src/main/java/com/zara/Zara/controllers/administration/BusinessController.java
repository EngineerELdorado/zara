package com.zara.Zara.controllers.administration;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Business;
import com.zara.Zara.services.IBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admins/businesses")
@RestController
@CrossOrigin(origins = "*")
public class BusinessController {

    @Autowired
    IBusinessService businessService;
    ApiResponse apiResponse = new ApiResponse();
    @GetMapping("/find-all")
    public ResponseEntity<?>findAll(@RequestParam int page,
                                    @RequestParam int size,
                                    @RequestParam Long start,
                                    @RequestParam Long end,
                                    @RequestParam String param){

        Page<Business>businesses = businessService.findAll(page,size,start,end,param);
//        apiResponse.setResponseCode("00");
        apiResponse.setData(businesses);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/block/{id}")
    public ResponseEntity<?>block(@PathVariable Long id){
        Business business = businessService.findOne(id);
        if (business.getStatus().equalsIgnoreCase("ACTIVE")){
            business.setStatus("INACTIVE");
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("COMPTE BLOQUE");
        }else if (business.getStatus().equalsIgnoreCase("INACTIVE")){
            business.setStatus("ACTIVE");
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("COMPTE DEBLOQUE");
        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
