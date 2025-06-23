package com.superlawva.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "네이버 로그인 요청 DTO")
public class NaverLoginRequestDTO {

    @NotBlank(message = "인가 코드는 필수입니다.")
    @Schema(description = "네이버 서버로부터 받은 인가 코드(Authorization Code)", requiredMode = Schema.RequiredMode.REQUIRED, example = "abcdefg1234567")
    private String authorizationCode;

    @NotBlank(message = "state 값은 필수입니다.")
    @Schema(description = "CSRF 공격 방지를 위한 상태 토큰", requiredMode = Schema.RequiredMode.REQUIRED, example = "hjklnmop890")
    private String state;

    private String email;
    private String name;
} 