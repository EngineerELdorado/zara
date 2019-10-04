package com.zara.Zara.controllers.administration;

import com.zara.Zara.constants.ApiResponse;
import com.zara.Zara.entities.PesapayTransaction;
import com.zara.Zara.services.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admins/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    ITransactionService transactionService;
    ApiResponse apiResponse = new ApiResponse();
    @GetMapping("/find-all")
    public ResponseEntity<?>findAll(@RequestParam int page, @RequestParam int size,
                                    @RequestParam Long start,@RequestParam Long end,
                                    @RequestParam String param){
        Page<PesapayTransaction>transactions = transactionService.findAll(page, size, start,end,param);
        apiResponse.setData(transactions);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/approve/{id}")
    public ResponseEntity<?> approveTransaction(Long id){

        PesapayTransaction transaction = transactionService.findOne(id);
        transaction.setStatus("APPROVED");
        apiResponse.setResponseCode("00");
        apiResponse.setResponseMessage("TRANSACTION APPROUVEE");
        // TODO: 03/10/2019  perform some processing logic here or send to RabbitMQ

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
