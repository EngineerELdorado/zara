package com.zara.Zara.controllers;

import com.zara.Zara.integrations.test.calculator.clients.CalculatorClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TestController {

    private final CalculatorClient calculatorClient;

    @GetMapping("/add")
    public ResponseEntity<?> add(@RequestParam int a, @RequestParam int b) {

        return new ResponseEntity<>(calculatorClient.addTwoNumbers(a, b), HttpStatus.OK);
    }

    @GetMapping("/subtract")
    public ResponseEntity<?> subtract(@RequestParam int a, @RequestParam int b) {

        return new ResponseEntity<>(calculatorClient.subtractTwoNumbers(a, b), HttpStatus.OK);
    }

    @GetMapping("/divide")
    public ResponseEntity<?> divide(@RequestParam int a, @RequestParam int b) {

        return new ResponseEntity<>(calculatorClient.divideTwoNumbers(a, b), HttpStatus.OK);
    }

    @GetMapping("/multiply")
    public ResponseEntity<?> multiply(@RequestParam int a, @RequestParam int b) {

        return new ResponseEntity<>(calculatorClient.multiplyTwoNumbers(a, b), HttpStatus.OK);
    }
}
