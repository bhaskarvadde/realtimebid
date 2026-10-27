package com.realtimebid.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.realtimebid.exception.EmailAlreadyExistsException;
import com.realtimebid.exception.InvalidCredentialsException;
import com.realtimebid.user.dto.LoginRequest;
import com.realtimebid.user.dto.RegisterRequest;

@Service
public class UserService {

	// Dependency required for password hashing and password verification.
	// Spring will provide the PasswordEncoder Bean that we created
	// in SecurityBeansConfig.
	private final PasswordEncoder passwordEncoder;

	// Dependency required to communicate with the database
	// for User-related operations.
	// Spring Data JPA provides the UserRepository Bean.
	private final UserRepository userRepository;

	// Constructor Injection:
	// Spring automatically provides the required dependencies
	// when it creates the UserService Bean.
	//
	// Spring finds:
	// 1. PasswordEncoder Bean → from SecurityBeansConfig
	// 2. UserRepository Bean → created by Spring Data JPA
	//
	// Then Spring passes both objects into this constructor.
	public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {

		// Store the injected PasswordEncoder so that
		// UserService can use it later.
		this.passwordEncoder = passwordEncoder;

		// Store the injected UserRepository so that
		// UserService can use it to access the database.
		this.userRepository = userRepository;
	}

	public User register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("Email already registered");
		}

		User user = new User();

		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(Role.USER);

		return userRepository.save(user);
	}

	public User login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Invalid credentials");
		}

		return user;
	}

}
