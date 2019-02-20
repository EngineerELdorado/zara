package com.zara.Zara.services.banking;
import com.zara.Zara.models.safepay.SafePayRequest;
import com.zara.Zara.models.safepay.SafepayResponse;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

import static com.zara.Zara.constants.ConstantVariables.SAFEPAY_PASSWORD;
import static com.zara.Zara.constants.ConstantVariables.SAFEPAY_USERNAME;


@Service
public class SafePayService {

    String username = SAFEPAY_USERNAME;
    String password = SAFEPAY_PASSWORD;

     public ResponseEntity<?> directDedit(SafePayRequest safePayRequest){
         RestTemplate restTemplate = new RestTemplate();
         ResponseEntity<?>responseEntity = restTemplate.exchange
                 ("https://api.test.paysafe.com/directdebit/v1/accounts/1001337150/purchases",
                         HttpMethod.POST, new HttpEntity<>(safePayRequest, createHeaders(username,password)),
                                 SafepayResponse.class);
         return  responseEntity;
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
