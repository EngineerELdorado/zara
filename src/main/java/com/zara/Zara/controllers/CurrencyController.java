package com.zara.Zara.controllers;

import com.zara.Zara.dtos.requests.ConversionRequest;
import com.zara.Zara.dtos.responses.CurrencyResponse;
import com.zara.Zara.resources.CurrencyResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    @PostMapping("/convert")
    public ResponseEntity<BigDecimal> convert(@RequestBody ConversionRequest request) {

        return new ResponseEntity<>(currencyResourceService.convert(request), HttpStatus.OK);
    }
}
