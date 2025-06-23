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
    private String name;
    private User.Role role;
    private LocalDateTime createdAt;
    private boolean emailVerified;

    public static UserResponseDTO of(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .emailVerified(user.isEmailVerified())
                .build();
    }

    public static UserResponseDTO from(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
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
        private String name;
    }
}
