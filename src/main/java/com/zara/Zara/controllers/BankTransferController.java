package com.zara.Zara.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zara.Zara.models.SafepayDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bankTransfers")
@CrossOrigin(origins = "*")
public class BankTransferController {

    @PostMapping("/post")
    public ResponseEntity<?>post(@RequestBody SafepayDto request) throws JsonProcessingException {

return null;
    }

}
