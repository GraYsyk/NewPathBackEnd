package com.graysenko.NewPathBackEnd.Services.User;

import com.graysenko.NewPathBackEnd.Entities.User.Role;
import com.graysenko.NewPathBackEnd.Repositories.User.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }
}
