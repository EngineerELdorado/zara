package com.zara.Zara.services.banking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zara.Zara.models.CallBackData;
import com.zara.Zara.models.PaySafeStatus;
import com.zara.Zara.models.transferwise.TransferWiseRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;


@Service
public class BusinessCallbackService {

    Logger LOGGER = LogManager.getLogger(SafePayService.class);
    RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<?> postData(CallBackData callBackData, String url) throws JsonProcessingException {

            ResponseEntity<Object>responseEntity = restTemplate.exchange
                    (url,
                            HttpMethod.POST, new HttpEntity<>(callBackData, null),
                            Object.class);

            return  new ResponseEntity<>(responseEntity.getBody(), HttpStatus.OK);



    }

}
