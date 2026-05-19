package com.graysenko.NewPathBackEnd.API;

import com.graysenko.NewPathBackEnd.DTOs.User.JwtResponse;
import com.graysenko.NewPathBackEnd.DTOs.User.LoginRequest;
import com.graysenko.NewPathBackEnd.DTOs.User.RegisterRequest;
import com.graysenko.NewPathBackEnd.DTOs.User.UserDTO;
import com.graysenko.NewPathBackEnd.Entities.User.Role;
import com.graysenko.NewPathBackEnd.Entities.User.User;
import com.graysenko.NewPathBackEnd.Services.User.UserService;
import com.graysenko.NewPathBackEnd.Util.JwtTokenUtils;
import com.graysenko.NewPathBackEnd.exceptions.AppError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new AppError(HttpStatus.UNAUTHORIZED.value(),
                            "Login or Password is invalid"));
        }

        User user = userService.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtTokenUtils.generateToken(user);
        String refreshToken = jwtTokenUtils.generateRefreshToken(user);
        return ResponseEntity.status(HttpStatus.OK).body(new JwtResponse(token,
                refreshToken,
                user.getName(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .toList(),
                user.getEmail()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.register(
                    registerRequest.getName(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword()
            );

            String jwt = jwtTokenUtils.generateToken(user);
            String refreshToken = jwtTokenUtils.generateRefreshToken(user);
            JwtResponse jwtResponse = new JwtResponse(
                    jwt,
                    refreshToken,
                    user.getName(),
                    user.getRoles().stream()
                            .map(Role::getName)
                            .toList(),
                    user.getEmail()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(jwtResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AppError(HttpStatus.CONFLICT.value(), e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> refreshRequest) {
        String token = refreshRequest.get("refreshToken");
        if (token == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), "Token is empty"));

        if (!jwtTokenUtils.isRefreshToken(token))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), "Token is not valid"));

        String email;
        try {
            email = jwtTokenUtils.getEmailFromToken(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AppError(HttpStatus.UNAUTHORIZED.value(), "Invalid refresh token"));
        }
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        String newAcessToken = jwtTokenUtils.generateToken(user);
        String newRefreshToken = jwtTokenUtils.generateRefreshToken(user);

        return ResponseEntity.ok(Map.of("token", newAcessToken, "refreshToken", newRefreshToken));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String  authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), "Token is empty"));

        String token = authHeader.substring(7);

        User user = userService.getUserFromToken(token);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AppError(HttpStatus.UNAUTHORIZED.value(), "Invalid token"));

        UserDTO dto = new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .toList(),
                user.getAddress()
                );
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(@RequestBody Map<String, String> updateRequest,
                                               @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AppError(HttpStatus.BAD_REQUEST.value(), "Token is empty"));

        String token = authHeader.substring(7);
        User user = userService.getUserFromToken(token);

        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AppError(HttpStatus.UNAUTHORIZED.value(), "Invalid token"));

        UserDTO dto = userService.updateUserDetails(user, updateRequest);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }
}
