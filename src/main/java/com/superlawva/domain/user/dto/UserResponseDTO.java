package com.superlawva.domain.user.dto;

import com.superlawva.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDTO {
    private Long   id;
    private String email;
    private String nickname;
    private String role;

    public static UserResponseDTO from(User u) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.id       = u.getId();
        dto.email    = u.getEmail();
        dto.nickname = u.getNickname();
        dto.role     = u.getRole().name();
        return dto;
    }
}
