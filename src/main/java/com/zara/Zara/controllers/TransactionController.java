package com.zara.Zara.controllers;

import com.zara.Zara.dtos.requests.TransactionRequest;
import com.zara.Zara.dtos.responses.TransactionResponse;
import com.zara.Zara.resources.TransactionResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionResourceService transactionResourceService;

    @PostMapping(value = "/accounts/{accountId}", headers = "Authorization")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransactionRequest request, @PathVariable Long accountId) {

        return new ResponseEntity<>(transactionResourceService.transfer(request, accountId), HttpStatus.CREATED);
    }

    @GetMapping(value = "/all", headers = "Authorization")
    public ResponseEntity<Page<TransactionResponse>> transferHistory(

            @RequestParam int page, @RequestParam int size,
            @RequestParam(value = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(value = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {

        return new ResponseEntity<>(transactionResourceService.history(page, size, startDate, endDate), HttpStatus.OK);
    }

    @GetMapping(value = "/accounts/{accountId}", headers = "Authorization")
    public ResponseEntity<Page<TransactionResponse>> transferHistoryByAccountId(

            @RequestParam Long accountId,
            @RequestParam int page, @RequestParam int size,
            @RequestParam(value = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(value = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {

        return new ResponseEntity<>(transactionResourceService.historyByAccountId(accountId, page, size, startDate, endDate), HttpStatus.OK);
    }

    @PostMapping(value = "/approve/{transactionId}", headers = "Authorization")
    public ResponseEntity<?> approvePendingTransaction(@PathVariable Long transactionId) {

        transactionResourceService.approve(transactionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/reject/{transactionId}", headers = "Authorization")
    public ResponseEntity<?> rejectPendingTransaction(@PathVariable Long transactionId) {

        transactionResourceService.reject(transactionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
