package com.realtimebid.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realtimebid.exception.InvalidCredentialsException;
import com.realtimebid.user.dto.UserResponse;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        UserResponse response = new UserResponse();
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setCreatedAt(user.getCreatedAt());
        return ResponseEntity.ok(response);
    }
}