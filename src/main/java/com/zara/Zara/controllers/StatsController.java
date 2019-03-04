package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Business;
import com.zara.Zara.entities.PesapayTransaction;
import com.zara.Zara.models.StatsRequest;
import com.zara.Zara.services.IBusinessService;
import com.zara.Zara.services.ICustomerService;
import com.zara.Zara.services.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

@RestController()
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    IBusinessService businessService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    ICustomerService customerService;
    ApiResponse apiResponse = new ApiResponse();

    Collection<PesapayTransaction>statsRecents;

    @PostMapping("/getByBusiness")
    public ResponseEntity<?> getStatsByBusiness(@RequestBody StatsRequest request) throws ParseException {

        Date start = new SimpleDateFormat("dd/MM/yyyy").parse(request.getStart());
        Date end = new SimpleDateFormat("dd/MM/yyyy").parse(request.getEnd());
        Business business = businessService.findByBusinessNumber(request.getBusinessNumber());

        apiResponse.setResponseCode("00");
        apiResponse.setResponseMessage("");
        apiResponse.setStatsSum(transactionService.allStatsSum(business.getId(),start, end));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


}
