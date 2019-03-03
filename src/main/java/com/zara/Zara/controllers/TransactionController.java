package com.zara.Zara.controllers;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.Business;
import com.zara.Zara.entities.Customer;
import com.zara.Zara.entities.PesapayTransaction;
import com.zara.Zara.services.IBusinessService;
import com.zara.Zara.services.ICustomerService;
import com.zara.Zara.services.ITransactionService;
import com.zara.Zara.services.IUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import static com.zara.Zara.constants.ConstantVariables.*;
import static com.zara.Zara.constants.Keys.*;
import static com.zara.Zara.constants.Responses.*;
import static com.zara.Zara.utils.BusinessNumbersGenerator.generateTransationNumber;
import static com.zara.Zara.utils.CheckingUtils.isAccountVerified;

@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    @Autowired
    IUserService userService;
    @Autowired
    ITransactionService transactionService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    IBusinessService businessService;
    Logger LOGGER = LogManager.getLogger(TransactionController.class);
    HttpHeaders responseHeaders = new HttpHeaders();
    ApiResponse apiResponse = new ApiResponse();
    @Autowired
    ICustomerService customerService;
    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(transactionService.getAll());
    }

    @GetMapping("/findByCustomerId/{phoneNumber}")
    public ResponseEntity<?>findByCustomerPhoneNumber(@PathVariable String phoneNumber){

     if (!phoneNumber.startsWith("+")){
         phoneNumber = "+"+phoneNumber;
     }
        Customer customer = customerService.findByPhoneNumber(phoneNumber);
     if (customer==null){
         apiResponse.setResponseCode("01");
         apiResponse.setResponseMessage("Client non trouve "+phoneNumber);
     }else {
         Collection<PesapayTransaction>transactions = transactionService.findByCustomerId(customer.getId());
         if (transactions.size()==0){
             apiResponse.setResponseCode("01");
             apiResponse.setResponseMessage("No Transactions found for "+phoneNumber);
         }else{
             apiResponse.setResponseCode("00");
             apiResponse.setResponseMessage("Transactions found");
             apiResponse.setTransactions(transactions);
         }
     }

        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    @GetMapping("/findCustomerEntries/{phoneNumber}")
    public ResponseEntity<?>findCustomerEntries(@PathVariable String phoneNumber){

        if (!phoneNumber.startsWith("+")){
            phoneNumber = "+"+phoneNumber;
        }
        Customer customer = customerService.findByPhoneNumber(phoneNumber);
        if (customer==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Client non trouve "+phoneNumber);
        }else {
            Collection<PesapayTransaction>transactions = transactionService.findCustomerEntries(customer.getId());
            if (transactions.size()==0){
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage("No Transactions found for "+phoneNumber);
            }else{
                apiResponse.setResponseCode("00");
                apiResponse.setResponseMessage("Transactions found");
                apiResponse.setTransactions(transactions);
            }
        }

        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    @GetMapping("/findCustomerOuts/{phoneNumber}")
    public ResponseEntity<?>findCustomerOuts(@PathVariable String phoneNumber){

        if (!phoneNumber.startsWith("+")){
            phoneNumber = "+"+phoneNumber;
        }
        Customer customer = customerService.findByPhoneNumber(phoneNumber);
        if (customer==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Client non trouve "+phoneNumber);
        }else {
            Collection<PesapayTransaction>transactions = transactionService.findCustomerOuts(customer.getId());
            if (transactions.size()==0){
                apiResponse.setResponseCode("01");
                apiResponse.setResponseMessage("No Transactions found for "+phoneNumber);
            }else{
                apiResponse.setResponseCode("00");
                apiResponse.setResponseMessage("Transactions found");
                apiResponse.setTransactions(transactions);
            }
        }

        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }

    @GetMapping("findByBusinessId/{businessNumber}")
    public ResponseEntity<?> findByBusinessId(
                                                     @PathVariable String businessNumber,
                                                     @RequestParam("type") String type,
                                                     @RequestParam("page") int page,
                                                     @RequestParam("size") int size){

        LOGGER.info("PAGE REQUEST PAGE =>"+page+" SIZE =>"+size);
        Business business = businessService.findByBusinessNumber(businessNumber);
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC,"id"));
         Pageable pageable = new PageRequest(page,size,sort);
        if (business==null){
            apiResponse.setResponseCode("01");
            apiResponse.setResponseMessage("Business introuvable");
        }else{

            apiResponse.setResponseCode("00");
            apiResponse.setResponseMessage(pageable.getPageSize()+" transactions");
            if (type.equals("all")){
                apiResponse.setTransactions(transactionService.findByBusiness(business.getId(), pageable).getContent());
            }else if(type.equals("entries")){
                apiResponse.setTransactions(transactionService.findEntriesByBusiness(business.getId(), pageable).getContent());
            }else if(type.equals("outs")){
                apiResponse.setTransactions(transactionService.findOutsByBusiness(business.getId(), pageable).getContent());
            }
            else if(type.equals("bulk")){
                apiResponse.setTransactions(transactionService.findBulkByBusiness(business.getId(), pageable).getContent());
            }

        }

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("findCountByBusiness/{businessNumber}")
    public int findCountByBusiness(@PathVariable String businessNumber, @RequestParam("type") String type){
        Business business = businessService.findByBusinessNumber(businessNumber);
        int count =0;
        if (type.equals("all")){
            count=transactionService.countByBusiness(business.getId());
        } else if (type.equals("entries")){
            count=transactionService.countEntriesByBusiness(business.getId());
        }
        else if (type.equals("outs")){
            count=transactionService.countOutsByBusiness(business.getId());
        }else if (type.equals("bulk")){
            count=transactionService.counBulkByBusiness(business.getId());
        }
        return count;
    }

}
