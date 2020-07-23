package com.zara.Zara;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class ZaraApplication  {
	Logger LOG = LogManager.getLogger(ZaraApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ZaraApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Scheduled(fixedRate = 600000)
	public void keepServerAlive(){

		final String uri = "https://pesapay-alpha.herokuapp.com/welcome";

		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(uri, String.class);
		LOG.info("KEEP_SERVER_ALIVE", result);
	}
}
