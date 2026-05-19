package com.graysenko.NewPathBackEnd.DTOs.User;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
