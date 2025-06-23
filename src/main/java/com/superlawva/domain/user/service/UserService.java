package com.superlawva.domain.user.service;

import com.superlawva.domain.user.dto.*;
import com.superlawva.domain.user.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<UserResponseDTO> findAll();
    UserResponseDTO findById(Long id);
    UserResponseDTO create(UserRequestDTO dto);
    UserResponseDTO update(Long id, UserRequestDTO dto);
    void delete(Long id);

    UserResponseDTO getMyInfo(Long userId);
    UserResponseDTO updateMyInfo(Long userId, UserRequestDTO.UpdateMyInfoDTO dto);
    void deleteMyAccount(Long userId);
    void changePassword(User user, PasswordChangeRequestDTO request);
    void processOAuth2User(String registrationId, Map<String,Object> attributes);
    UserResponseDTO signUp(UserRequestDTO.SignUpDTO request);
    LoginResponseDTO login(LoginRequestDTO request);
    LoginResponseDTO kakaoLogin(KakaoLoginRequestDTO request);
    LoginResponseDTO naverLogin(NaverLoginRequestDTO request);
    UserResponseDTO.MyPageDTO getMyPage(User user);

    User findUserById(Long userId);

    void checkEmailDuplication(String email);

    User findByEmail(String email);

    List<UserResponseDTO> getUsers(String keyword);
}
