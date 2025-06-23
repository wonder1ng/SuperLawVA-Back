package com.superlawva.domain.user.service;

import com.superlawva.domain.user.dto.*;
import com.superlawva.domain.user.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    void register(UserRequestDTO userRequestDTO);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    LoginResponseDTO kakaoLogin(KakaoLoginRequestDTO kakaoLoginRequestDTO);

    LoginResponseDTO naverLogin(NaverLoginRequestDTO naverLoginRequestDTO);

    User findByEmail(String email);

    void changePassword(User user, PasswordChangeRequestDTO request);

    UserResponseDTO getMyInfo(Long userId);
    
    LoginResponseDTO getUserDashboard(Long userId);
    
    UserResponseDTO updateMyInfo(Long userId, UserRequestDTO.UpdateMyInfoDTO request);
    
    void deleteMyAccount(Long userId);
    
    List<UserResponseDTO> findAll();
    
    UserResponseDTO findById(Long id);
    
    UserResponseDTO update(Long id, UserRequestDTO dto);
    
    void delete(Long id);
    
    User processOAuth2User(String registrationId, Map<String, Object> attributes);
}
