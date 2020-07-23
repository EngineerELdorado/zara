package com.zara.Zara;

import com.zara.Zara.entities.Country;
import com.zara.Zara.entities.Currency;
import com.zara.Zara.repositories.CountryRepository;
import com.zara.Zara.repositories.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ZaraApplication {

	private final CountryRepository countryRepository;
	private final CurrencyRepository currencyRepository;

	public static void main(String[] args) {
		SpringApplication.run(ZaraApplication.class, args);

	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Scheduled(fixedRate = 600000)
	public void keepServerAlive() {

		final String uri = "https://pesapay-alpha.herokuapp.com/welcome";

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getForObject(uri, String.class);
	}

}
