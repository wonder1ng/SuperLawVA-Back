package com.superlawva.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "소셜 로그인 임시 정보 DTO - 이메일 입력 필요 시 반환")
public class SocialLoginTempDTO {
    
    @Schema(description = "임시 토큰 (이메일 입력 완료 시 사용)", example = "temp_token_abc123")
    private String tempToken;
    
    @Schema(description = "소셜 플랫폼에서 받은 닉네임", example = "카카오사용자")
    private String nickname;
    
    @Schema(description = "소셜 로그인 제공자", example = "KAKAO")
    private String provider;
    
    @Schema(description = "이메일 입력 필요 여부", example = "true")
    private boolean needEmail;
    
    @Schema(description = "안내 메시지", example = "카카오에서 이메일 정보를 제공하지 않습니다. 이메일을 입력해주세요.")
    private String message;
} 