package com.graysenko.NewPathBackEnd.DTOs.User;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String name;
    private List<String> roles;
    private String email;
}
