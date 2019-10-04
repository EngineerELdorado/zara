package com.zara.Zara.controllers.administration;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.models.Stat;
import com.zara.Zara.services.IAgentService;
import com.zara.Zara.services.IBusinessService;
import com.zara.Zara.services.ICustomerService;
import com.zara.Zara.services.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admins/stats")
@CrossOrigin(origins = "*")
public class _StatsController {

    @Autowired
    IBusinessService businessService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    IAgentService agentService;
    @Autowired
    ICustomerService customerService;
    ApiResponse apiResponse = new ApiResponse();
    @GetMapping("/get")
    public ResponseEntity<?>getStat(Long start, Long end){

        Stat stat = new Stat();
        stat.setAgents(agentService.findCount(start,end));
        stat.setTransactions(transactionService.findCount(start,end));
        stat.setCustomers(customerService.findCount(start,end));
        stat.setBusinesses(businessService.findCount(start,end));
        stat.setTransactions(businessService.findCount(start,end));
        stat.setAmounts(transactionService.amounts(start,end));
        stat.setCommissions(transactionService.commissions(start,end));
        apiResponse.setData(stat);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
