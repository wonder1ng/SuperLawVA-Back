package com.superlawva.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequestDTO {

    @NotBlank(message = "기존 비밀번호를 입력해주세요.")
    @Schema(description = "기존 비밀번호", example = "password123")
    private String oldPassword;

    @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
    @Schema(description = "새로운 비밀번호", example = "newPassword456")
    private String newPassword;
} 