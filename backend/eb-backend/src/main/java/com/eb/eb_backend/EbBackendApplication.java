package com.eb.eb_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EbBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EbBackendApplication.class, args);
	}

}
