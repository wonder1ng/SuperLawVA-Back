package com.superlawva.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequestDTO {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    @Schema(description = "현재 비밀번호", example = "currentPassword123!")
    private String currentPassword;

    @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
    @Schema(description = "새로운 비밀번호", example = "newPassword123!")
    private String newPassword;

    @NotBlank(message = "새 비밀번호 확인을 입력해주세요.")
    @Schema(description = "새 비밀번호 확인", example = "newPassword123!")
    private String confirmNewPassword;
} 