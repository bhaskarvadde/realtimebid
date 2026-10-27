package com.realtimebid.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realtimebid.security.JwtUtil;
import com.realtimebid.user.dto.AuthResponse;
import com.realtimebid.user.dto.LoginRequest;
import com.realtimebid.user.dto.RegisterRequest;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	private final UserService userService;
	private final JwtUtil jwtUtil;

	public AuthController(UserService userService, JwtUtil jwtUtil) {
		this.userService = userService;
		this.jwtUtil = jwtUtil;
	}

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
		User createdUser = userService.register(request);

		return ResponseEntity.status(HttpStatus.CREATED).body("Registered Successfully " + createdUser.getEmail());
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
		User user = userService.login(request);
		String token = jwtUtil.generateToken(user.getEmail());

		AuthResponse response = new AuthResponse();
		response.setToken(token);

		return ResponseEntity.ok(response);

	}

}
