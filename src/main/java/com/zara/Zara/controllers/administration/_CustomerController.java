package com.zara.Zara.controllers.administration;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Business;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.services.IBusinessService;
import com.zara.Zara.services.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admins/customers")
@RestController
@CrossOrigin(origins = "*")
public class _CustomerController {

    @Autowired
    ICustomerService customerService;
    ApiResponse apiResponse = new ApiResponse();
    @GetMapping("/find-all")
    public ResponseEntity<?>findAll(@RequestParam int page,
                                    @RequestParam int size,
                                    @RequestParam Long start,
                                    @RequestParam Long end,
                                    @RequestParam String param){

        Page<Customer>customers = customerService.findAll(page,size,start,end,param);
//        apiResponse.setResponseCode("00");
        apiResponse.setData(customers);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/block/{id}")
    public ResponseEntity<?>block(@PathVariable Long id){
        Customer customer = customerService.findOne(id);
        if (customer.getStatus().equalsIgnoreCase("ACTIVE")){
            customer.setStatus("INACTIVE");
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("COMPTE BLOQUE");
        }else if (customer.getStatus().equalsIgnoreCase("INACTIVE")){
            customer.setStatus("ACTIVE");
            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage("COMPTE DEBLOQUE");
        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
