package com.superlawva.global.security.handler;

import com.superlawva.global.security.util.JwtTokenProvider;
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
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // OAuth2User 에서 email 꺼내오기
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        // 토큰 생성
        String token = jwtProvider.createToken(email);

        // 프론트엔드 URL로 리다이렉트 (환경변수로 설정 가능)
        String frontendUrl = "http://localhost:3000"; // 개발용, 운영시에는 실제 도메인
        String redirectUrl = frontendUrl + "/login/success?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
