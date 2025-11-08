package com.comecan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.comecan.*"})
public class ComecanBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComecanBackendApplication.class, args);
	}

}
