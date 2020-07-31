package com.zara.Zara.controllers;


import com.zara.Zara.dtos.requests.CommissionTransferRequest;
import com.zara.Zara.resources.CommissionResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/commissions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CommissionController {

    private final CommissionResourceService commissionResourceService;

    @PostMapping(value = "/accounts/{accountId}/transfer", headers = "Authorization")
    public ResponseEntity<?> transferCommissionToAccount(@PathVariable Long accountId,
                                                         @RequestBody CommissionTransferRequest request) {

        commissionResourceService.transfer(accountId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
