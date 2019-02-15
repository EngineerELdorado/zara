package com.zara.Zara.services.utils;

import com.zara.Zara.controllers.CustomerController;
import com.zara.Zara.models.Sms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static com.zara.Zara.constants.ConstantVariables.*;

public class SmsService {
    static Logger LOG = LogManager.getLogger(SmsService.class);
    public static void sendSms(Sms sms){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BUDGET_SMS_URL)
                .queryParam("username", BUDGET_SMS_USERNAME)
                .queryParam("userid", BUDGET_SMS_USER_ID)
                .queryParam("handle", BUDGET_SMS_HANDLE)
                .queryParam("msg", sms.getMessage())
                .queryParam("from", BUDGET_SMS_USER_ID)
                .queryParam("to", sms.getTo());

        HttpEntity<?> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class);
        LOG.info("BUDGET_SMS_RESPONSE==> "+response.toString());

    }
}
