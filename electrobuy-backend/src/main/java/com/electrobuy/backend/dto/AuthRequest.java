package com.electrobuy.backend.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}