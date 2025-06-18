package com.superlawva.domain.user.service;

import com.superlawva.domain.user.dto.UserRequestDTO;
import com.superlawva.domain.user.dto.UserResponseDTO;
import com.superlawva.domain.user.dto.PasswordChangeRequestDTO;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<UserResponseDTO> findAll();
    UserResponseDTO findById(Long id);
    UserResponseDTO create(UserRequestDTO dto);      // ← create 메서드 시그니처
    UserResponseDTO update(Long id, UserRequestDTO dto);
    void delete(Long id);
    UserResponseDTO getMyInfo(String email);

    void processOAuth2User(String registrationId, Map<String,Object> attributes);
    void changePassword(String email, PasswordChangeRequestDTO dto);
}
