package com.superlawva.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "소셜 로그인 완료 요청 DTO")
public class SocialLoginCompleteDTO {
    
    @NotBlank(message = "임시 토큰은 필수입니다.")
    @Schema(description = "소셜 로그인 1단계에서 받은 임시 토큰", requiredMode = Schema.RequiredMode.REQUIRED, example = "temp_token_abc123")
    private String tempToken;
    
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Schema(description = "사용자가 입력한 이메일", requiredMode = Schema.RequiredMode.REQUIRED, example = "user@example.com")
    private String email;
} 