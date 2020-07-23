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

	@EventListener(ApplicationReadyEvent.class)
	public void initializeSomeData() {
		List<Country> countries = new ArrayList<>();
		Country kenya = new Country("Kenya", "KE");
		Country burundi = new Country("Burundi", "BU");
		Country uganda = new Country("Uganda", "UG");
		Country tanzania = new Country("Tanzania", "TZ");
		Country rwanda = new Country("Rwanda", "RW");
		Country congo = new Country("Congo", "CD");
		countries.add(kenya);
		countries.add(burundi);
		countries.add(uganda);
		countries.add(tanzania);
		countries.add(rwanda);
		countries.add(congo);

		if (countryRepository.findAll().isEmpty()) {
			countryRepository.saveAll(countries);
		}

		List<Currency> currencies = new ArrayList<>();
		Currency kenyanShilling = new Currency("Kenyan Shilling", "KSH");
		Currency burundianFrancs = new Currency("Burundian Francs", "BUF");
		Currency ugandanShillings = new Currency("Ugandan Shillings", "USH");
		Currency tanzanianShillings = new Currency("Tanzania", "TSH");
		Currency rwandanFrancs = new Currency("Rwanda", "FRW");
		Currency congoleseFrancs = new Currency("Congo", "FC");
		currencies.add(kenyanShilling);
		currencies.add(burundianFrancs);
		currencies.add(ugandanShillings);
		currencies.add(tanzanianShillings);
		currencies.add(rwandanFrancs);
		currencies.add(congoleseFrancs);

		if (currencyRepository.findAll().isEmpty()) {
			currencyRepository.saveAll(currencies);
		}

		log.info("Loaded initial data");
	}
}
