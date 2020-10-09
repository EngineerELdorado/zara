package com.zara.Zara.integrations.test.countries.client;

import com.zara.Zara.integrations.test.countries.GetCountryRequest;
import com.zara.Zara.integrations.test.countries.GetCountryResponse;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

@Service
public class CountryClient extends WebServiceGatewaySupport {

    public GetCountryResponse getCountry(String country) {
        GetCountryRequest request = new GetCountryRequest();
        request.setName(country);

        return (GetCountryResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
    }
}
