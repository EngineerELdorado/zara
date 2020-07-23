package com.zara.Zara;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class PesaPay {

	public static void main(String[] args) {
		SpringApplication.run(PesaPay.class, args);
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
