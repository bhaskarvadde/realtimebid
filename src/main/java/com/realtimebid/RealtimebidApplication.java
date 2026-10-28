package com.realtimebid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RealtimebidApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealtimebidApplication.class, args);
	}

}
