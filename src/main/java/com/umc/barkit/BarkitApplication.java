package com.umc.barkit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BarkitApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarkitApplication.class, args);
	}

}
