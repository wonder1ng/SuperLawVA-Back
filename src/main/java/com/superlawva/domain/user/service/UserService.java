package com.superlawva.domain.user.service;

import com.superlawva.domain.user.dto.*;
import com.superlawva.domain.user.entity.User;

public interface UserService {
    void register(UserRequestDTO userRequestDTO);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    LoginResponseDTO kakaoLogin(KakaoLoginRequestDTO kakaoLoginRequestDTO);

    LoginResponseDTO naverLogin(NaverLoginRequestDTO naverLoginRequestDTO);

    User findByEmail(String email);

    void changePassword(User user, PasswordChangeRequestDTO request);
}
