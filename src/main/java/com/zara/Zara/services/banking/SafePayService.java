package com.zara.Zara.services.banking;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.zara.Zara.controllers.CustomerTransferController;
import com.zara.Zara.models.safepay.SafePayRequest;
import com.zara.Zara.models.safepay.SafepayResponse;
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

     public ResponseEntity<?> directDedit(SafePayRequest safePayRequest) throws JsonProcessingException {
         RestTemplate restTemplate = new RestTemplate();


         ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
         String json = ow.writeValueAsString(safePayRequest);
         ResponseEntity<?>responseEntity = restTemplate.exchange
         ("https://api.test.paysafe.com/directdebit/v1/accounts/1001337150/purchases",
          HttpMethod.POST, new HttpEntity<>(json, createHeaders(username,password)),
         SafepayResponse.class);
         LOGGER.info("PAYSAFE_REQUEST", json);
         return  new ResponseEntity<>(responseEntity, HttpStatus.OK);
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

}
