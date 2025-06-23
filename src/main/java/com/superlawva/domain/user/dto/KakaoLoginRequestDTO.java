package com.superlawva.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "카카오 로그인 요청 DTO")
public class KakaoLoginRequestDTO {

    @NotBlank(message = "인가 코드는 필수입니다.")
    @Schema(description = "카카오 서버로부터 받은 인가 코드(Authorization Code)", requiredMode = Schema.RequiredMode.REQUIRED, example = "abcdefg1234567hijklmnop890")
    private String authorizationCode;
} 