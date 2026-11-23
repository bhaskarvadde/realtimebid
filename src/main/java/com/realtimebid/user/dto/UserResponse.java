package com.realtimebid.user.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserResponse {
    private String name;
    private String email;
    private String role;
    private LocalDateTime createdAt;
}