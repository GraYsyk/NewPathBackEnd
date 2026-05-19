package com.graysenko.NewPathBackEnd.Configuration;

import com.graysenko.NewPathBackEnd.Services.User.UserService;
import com.graysenko.NewPathBackEnd.Util.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String email = null;
        String jwtToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
             jwtToken = authorizationHeader.substring("Bearer ".length());
            if (jwtToken.equals("null") || jwtToken.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }
            try {
                email = jwtTokenUtils.getEmailFromToken(jwtToken);
            } catch (ExpiredJwtException | SecurityException e) {
                logger.warn("Invalid JWT Token", e);
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
