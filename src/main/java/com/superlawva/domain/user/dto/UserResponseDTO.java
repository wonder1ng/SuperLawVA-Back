package com.superlawva.domain.user.dto;

import com.superlawva.domain.user.entity.User;
import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserResponseDTO {
    private Long   id;
    private String email;
    private String userName;  // 프론트엔드 호환을 위해 userName으로 변경
    private User.Role role;
    private LocalDateTime createdAt;
    private boolean emailVerified;

    public static UserResponseDTO of(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userName(user.getNickname())  // nickname을 userName으로 매핑
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .emailVerified(user.isEmailVerified())
                .build();
    }

    public static UserResponseDTO from(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userName(user.getNickname())  // nickname을 userName으로 매핑
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .emailVerified(user.isEmailVerified())
                .build();
    }

    @Getter
    @Builder
    public static class MyPageDTO {
        private Long id;
        private String email;
        private String userName;  // 프론트엔드 호환을 위해 userName 사용
    }
}
