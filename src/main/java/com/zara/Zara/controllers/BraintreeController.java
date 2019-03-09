package com.zara.Zara.controllers;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.braintreegateway.Environment;
import com.zara.Zara.constants.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.GeneratedValue;

import static com.zara.Zara.constants.ConstantVariables.*;

@RestController
@RequestMapping("/braintree")
@CrossOrigin(origins = "*")
public class BraintreeController {

    ApiResponse apiResponse = new ApiResponse();


    BraintreeGateway gateway = new BraintreeGateway(
            Environment.SANDBOX,
            BRAINTREE_MERCHANT_ID,
            BRAINTREE_PUBLIC_KEY,
            BRAINTREE_SECRET_KEY
    );
    @GetMapping("/generateToken")
    public ResponseEntity<?>generateToken(){

        ClientTokenRequest clientTokenRequest = new ClientTokenRequest();
        String clientToken = gateway.clientToken().generate(clientTokenRequest);
        apiResponse.setResponseCode("00");
        apiResponse.setResponseMessage(clientToken);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
