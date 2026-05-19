package com.graysenko.NewPathBackEnd.DTOs.User;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private List<String> roles;
    private String address;
}
