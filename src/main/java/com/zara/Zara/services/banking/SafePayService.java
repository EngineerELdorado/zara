package com.zara.Zara.services.banking;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.zara.Zara.models.PaySafeStatus;
import com.zara.Zara.models.transferwise.TransferWiseRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

import static com.zara.Zara.constants.ConstantVariables.SAFEPAY_PASSWORD;
import static com.zara.Zara.constants.ConstantVariables.SAFEPAY_USERNAME;


@Service
public class SafePayService {

    String username = SAFEPAY_USERNAME;
    String password = SAFEPAY_PASSWORD;
    Logger LOGGER = LogManager.getLogger(SafePayService.class);
    RestTemplate restTemplate = new RestTemplate();

     public ResponseEntity<?> directDedit(TransferWiseRequest transferWiseRequest) throws JsonProcessingException {

         if (isServiceAvailable()){
             ResponseEntity<SafepayResponse>responseEntity = restTemplate.exchange
                     ("https://api.test.paysafe.com/directdebit/v1/accounts/1001337150/purchases",
                             HttpMethod.POST, new HttpEntity<>(transferWiseRequest, createHeaders(username,password)),
                             SafepayResponse.class);

             return  new ResponseEntity<>(responseEntity.getBody(), HttpStatus.OK);
         }else{
             return  new ResponseEntity<>(null, HttpStatus.OK);
         }


     }


    HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")) );
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }

    public boolean isServiceAvailable(){
        ResponseEntity<PaySafeStatus>responseEntity = restTemplate.exchange
                ("https://api.test.paysafe.com/directdebit/monitor",
                        HttpMethod.GET, null,
                        PaySafeStatus.class);
        LOGGER.info(responseEntity.getBody().getStatus());
        String status =responseEntity.getBody().getStatus();
        if (status.equals("READY")){
            return true;
        }else{
            return false;
        }
    }

}
