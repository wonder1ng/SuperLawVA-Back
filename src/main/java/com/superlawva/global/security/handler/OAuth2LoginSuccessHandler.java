package com.superlawva.global.security.handler;

import com.superlawva.global.security.util.JwtTokenProvider;
import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    public OAuth2LoginSuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse res,
                                        Authentication auth) {
        String email = auth.getName();
        String token = jwtTokenProvider.createToken(email);
        res.setHeader("Authorization", "Bearer " + token);
        res.setStatus(HttpServletResponse.SC_OK);
    }
}
