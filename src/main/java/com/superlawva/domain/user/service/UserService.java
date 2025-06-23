package com.superlawva.domain.user.service;

import com.superlawva.domain.user.dto.LoginRequestDTO;
import com.superlawva.domain.user.dto.LoginResponseDTO;
import com.superlawva.domain.user.dto.PasswordChangeRequestDTO;
import com.superlawva.domain.user.dto.UserRequestDTO;
import com.superlawva.domain.user.dto.UserResponseDTO;

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
    void changePassword(Long userId, PasswordChangeRequestDTO dto);
    void processOAuth2User(String registrationId, Map<String,Object> attributes);
    UserResponseDTO signUp(UserRequestDTO.SignUpDTO request);
    LoginResponseDTO login(LoginRequestDTO request);
}
