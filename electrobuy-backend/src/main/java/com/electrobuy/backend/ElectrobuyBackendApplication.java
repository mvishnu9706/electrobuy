package com.electrobuy.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // This enables the @CreatedDate and @LastModifiedDate features
public class ElectrobuyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElectrobuyBackendApplication.class, args);
	}

}