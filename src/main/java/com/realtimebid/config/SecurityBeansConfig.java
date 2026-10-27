package com.realtimebid.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityBeansConfig {

	// @Bean tells Spring to create and manage the object
	// returned by this method as a Spring Bean.
	//
	// PasswordEncoder is an interface.
	// BCryptPasswordEncoder is the actual implementation
	// that will be used for hashing passwords.
	@Bean
	public PasswordEncoder passwordEncoder() {

		// Create and return a BCryptPasswordEncoder object.
		// Spring stores this object in the Spring Container.
		//
		// Later, any class that needs a PasswordEncoder
		// can receive this Bean through Dependency Injection.
		return new BCryptPasswordEncoder();
	}
}
