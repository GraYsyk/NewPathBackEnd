package com.graysenko.NewPathBackEnd.Configuration.Oauth;

import com.graysenko.NewPathBackEnd.Entities.User.User;
import com.graysenko.NewPathBackEnd.Services.User.UserService;
import com.graysenko.NewPathBackEnd.Util.JwtTokenUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email =  oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userService.findByEmail(email).orElseGet(
                () -> userService.registerOauthUser(email, name)
        );

        String accessToken = jwtTokenUtils.generateToken(user);
        String refreshToken = jwtTokenUtils.generateRefreshToken(user);

        String redirectUrl = "http://localhost:5173/oauth2/popup-callback" +
                "?token=" + accessToken +
                "&refreshToken=" + refreshToken;

        response.sendRedirect(redirectUrl);
    }
}
