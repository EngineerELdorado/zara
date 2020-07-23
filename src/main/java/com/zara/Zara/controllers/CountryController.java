package com.zara.Zara.controllers;

import com.zara.Zara.dtos.responses.CountryResponse;
import com.zara.Zara.resources.CountryResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/countries")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CountryController {

    private final CountryResourceService countryResourceService;

    @GetMapping("")
    public ResponseEntity<List<CountryResponse>> findAll() {

        return new ResponseEntity<>(countryResourceService.findAll(), HttpStatus.OK);
    }
}
