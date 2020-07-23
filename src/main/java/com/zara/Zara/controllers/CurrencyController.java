package com.zara.Zara.controllers;

import com.zara.Zara.dtos.responses.CurrencyResponse;
import com.zara.Zara.resources.CurrencyResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/currencies")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyResourceService currencyResourceService;

    @GetMapping("")
    public ResponseEntity<List<CurrencyResponse>> findAll() {

        return new ResponseEntity<>(currencyResourceService.findAll(), HttpStatus.OK);
    }
}
