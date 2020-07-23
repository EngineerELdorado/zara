package com.zara.Zara.services;

import com.zara.Zara.dtos.responses.OpenExchangeRateResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OpenExchangeRateService {

    final String uri = "https://openexchangerates.org/api/latest.json?app_id=ae2c8263bcd7436ab3b20b8e8efdbb92";

    public OpenExchangeRateResponse getExchangeRates() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(uri, OpenExchangeRateResponse.class);
        } catch (Exception e) {
            String msg = "An error has occurred while retrieving exchange rates, possible reason(s) : "
                    + ExceptionUtils.getRootCauseMessage(e);
            log.error(msg);
            throw e;
        }
    }
}
