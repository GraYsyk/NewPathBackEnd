package com.graysenko.NewPathBackEnd.Services.User;

import com.graysenko.NewPathBackEnd.DTOs.User.UserDTO;
import com.graysenko.NewPathBackEnd.Entities.User.Role;
import com.graysenko.NewPathBackEnd.Entities.User.User;
import com.graysenko.NewPathBackEnd.Repositories.User.UserRepository;
import com.graysenko.NewPathBackEnd.Util.JwtTokenUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils  jwtTokenUtils;

    @Transactional
    public User register(String name, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User with email " + email + " already exists!");
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRoles(List.of(roleService.findByName("ROLE_USER").get()));
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                "User with email " + email + " not found!"
        ));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );
    }

    @Transactional
    public User registerOauthUser(String email, String name) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("");
        return userRepository.save(user);
    }

    @Transactional
    public UserDTO updateUserDetails(User user, Map<String, String> userDetails) {
        user.setName(userDetails.get("name"));
        user.setEmail(userDetails.get("email"));
        user.setAddress(userDetails.get("address"));
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .toList(),
                user.getAddress()
        );
    }


    public User getUserFromToken(String token) {
        String email;
        try {
            email = jwtTokenUtils.getEmailFromToken(token);
        } catch (Exception e) {
            return null; //INVALID TOKEN
        }
        return findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
