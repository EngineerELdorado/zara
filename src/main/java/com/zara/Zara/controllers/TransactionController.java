package com.zara.Zara.controllers;

import com.zara.Zara.dtos.requests.TransactionRequest;
import com.zara.Zara.dtos.responses.TransactionResponse;
import com.zara.Zara.resources.TransactionResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
}
