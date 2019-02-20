package com.zara.Zara.services.banking;
import com.zara.Zara.models.safepay.SafepayResponse;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;


@Service
public class SafePayService {

    String username = "denis_kalenga94";
    String password = "B-qa2-0-5c6d1215-0-302d021462615dd333224755bacc584fc88aaffac16c38e1021500945f9a1e3381861f9ab16e1de49f1aab4c3246c5";

     public ResponseEntity<?> directDedit(String accountNumber, float amount){
         RestTemplate restTemplate = new RestTemplate();
         ResponseEntity<?>responseEntity = restTemplate.exchange
                 ("https://api.test.paysafe.com/directdebit/v1/accounts/1001337150/purchases",
                         HttpMethod.POST, new HttpEntity<>(createHeaders(username,password)),
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
