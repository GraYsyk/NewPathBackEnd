package com.graysenko.NewPathBackEnd.Util;

import com.graysenko.NewPathBackEnd.Entities.User.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.refreshlifetime}")
    private Duration refreshLifetime;

    @Value("${jwt.lifetime}")
    private Duration jwtLifeTIme;

    public String generateToken(User user) {
        return generateAccessToken(user, jwtLifeTIme, "accessToken");
    }

    public String generateRefreshToken(User user) {
        return generateAccessToken(user, refreshLifetime, "refreshToken");
    }

    private String generateAccessToken(User user, Duration tokenLifetime, String type) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("type", type);
        claims.put("name", user.getName());
        claims.put("roles", user.getRoles().stream()
                .map(role -> role.getName()).toList());

        Date issuedAt = new Date();
        Date expirationDate = new Date(issuedAt.getTime() + tokenLifetime.toMillis());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(issuedAt)
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        return getClaimsFromToken(token).get("roles", List.class);
    }

    public boolean isRefreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return "refreshToken".equals(claims.get("type"));
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
