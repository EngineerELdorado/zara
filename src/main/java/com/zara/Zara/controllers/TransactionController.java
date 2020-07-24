package com.zara.Zara.controllers;

import com.zara.Zara.dtos.requests.TransactionRequest;
import com.zara.Zara.resources.TransactionResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionResourceService transactionResourceService;

    @PostMapping(value = "", headers = "Authorization")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransactionRequest request) {

        return null;
    }
}
