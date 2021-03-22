package com.zara.Zara.integrations.test.calculator.clients;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class CalculatorClientConfig {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.zara.Zara.integrations.test.calculator_service");
        return marshaller;
    }
    @Bean
    public CalculatorClient countryClient(Jaxb2Marshaller marshaller) {
        CalculatorClient client = new CalculatorClient();
        client.setDefaultUri("http://tempuri.org/Add");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
