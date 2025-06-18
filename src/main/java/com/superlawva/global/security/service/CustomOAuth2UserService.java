package com.superlawva.global.security.service;

import com.superlawva.domain.user.service.UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        OAuth2User oauth2User = super.loadUser(request);

        // 어느 공급자인지 (e.g. "kakao", "naver")
        String regId = request.getClientRegistration().getRegistrationId();
        // 유저 속성(attribute) 맵을 서비스로 전달
        userService.processOAuth2User(regId, oauth2User.getAttributes());

        return oauth2User;
    }
}
