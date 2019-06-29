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

import java.text.ParseException;
import java.util.Collection;

@RestController()
@RequestMapping("/stats")
@CrossOrigin(origins = "*")
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
    public ResponseEntity<?> getStatsByBusiness(@RequestBody StatsRequest statsRequest) throws ParseException {

        Business business = businessService.findByBusinessNumber(statsRequest.getBusinessNumber());

        apiResponse.setResponseCode("00");
        apiResponse.setResponseMessage("");
        apiResponse.setAllStatsSum(transactionService.allStatsSumByBusiness(business.getId()));
        apiResponse.setEntriesStatsSum(transactionService.entriesStatsSumByBusiness(business.getId()));
        apiResponse.setOutsStatsSum(transactionService.outsStatsSumByBusiness(business.getId()));
        apiResponse.setAllRecentTransactions(transactionService.allStatsTransactionsByBusiness(business.getId()));
        apiResponse.setEntriesRecentTransactions(transactionService.entriesStatsTransactionsByBusiness(business.getId()));
        apiResponse.setOutsRecentTransactions(transactionService.outsStatsTransactionsByBusiness(business.getId()));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


}
