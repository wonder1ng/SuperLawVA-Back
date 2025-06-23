package com.superlawva.global.security.controller;

import com.superlawva.domain.user.entity.User;
import com.superlawva.domain.user.repository.UserRepository;
import com.superlawva.global.exception.BaseException;
import com.superlawva.global.response.ApiResponse;
import com.superlawva.global.response.status.ErrorStatus;
import com.superlawva.global.security.util.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "⚠️ Test & Tools", description = "개발 및 테스트용 API")
@RequiredArgsConstructor
@RestController
public class TokenController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Operation(summary = "테스트용 소셜 로그인 JWT 발급",
            description = """
            ### ⚠️ 개발 및 테스트 전용 API (프론트엔드 사용 안내)
            - **이 API는 실제 서비스 로직에 포함되면 안 됩니다.**
            
            #### 🤔 이 API는 언제 사용하나요?
            - 프론트엔드에서 **인증(JWT)이 필요한 다른 API들을 테스트**하고 싶을 때, 매번 번거롭게 소셜 로그인을 할 필요 없이 이 API를 통해 **즉시 유효한 테스트용 JWT 토큰**을 발급받을 수 있습니다.
            
            #### ✅ 사용 방법
            1. 백엔드 개발자에게 **테스트용 계정의 소셜 ID**를 요청하세요. (예: 카카오 ID `1234567890`)
            2. 아래 Parameters에 `provider`와 `socialId`를 입력하고 **"Execute"** 버튼을 누릅니다.
            3. **Response body**에 반환된 JWT 토큰 문자열을 복사합니다.
            4. Swagger 우측 상단의 **"Authorize"** 버튼을 누르고, `Bearer {복사한_토큰}` 형식으로 붙여넣어 인증을 완료합니다.
            5. 이제 자물쇠(🔒)가 걸린 모든 API를 자유롭게 테스트할 수 있습니다.
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 발급 성공", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "eyJhbGciOiJIUzI1NiJ9..."))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (지원하지 않는 프로바이더 또는 잘못된 ID 형식)", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 소셜 ID를 가진 사용자를 찾을 수 없음", content = @Content)
    })
    @GetMapping("/token/social/{provider}/{socialId}")
    public ResponseEntity<String> socialToken(
            @Parameter(description = "소셜 로그인 프로바이더 (kakao 또는 naver)", example = "kakao") @PathVariable String provider,
            @Parameter(description = "사용자의 소셜 ID", example = "1234567890") @PathVariable String socialId) {

        Optional<User> userOptional;

        if ("kakao".equalsIgnoreCase(provider)) {
            try {
                userOptional = userRepository.findByKakaoId(Long.parseLong(socialId));
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Kakao ID는 숫자(Long) 형식이어야 합니다.");
            }
        } else if ("naver".equalsIgnoreCase(provider)) {
            userOptional = userRepository.findByNaverId(socialId);
        } else {
            return ResponseEntity.badRequest().body("지원하지 않는 프로바이더입니다. (kakao, naver 중 선택)");
        }

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = jwtTokenProvider.createToken(user.getEmail());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 ID의 사용자를 찾을 수 없습니다.");
        }
    }

    @PostMapping("/regenerate")
    public ApiResponse<Map<String, String>> regenerateToken(HttpServletRequest request) {
        // 1. "Authorization" 헤더에서 리프레시 토큰 추출
        String refreshToken = jwtTokenProvider.resolveToken(request);

        // 2. 리프레시 토큰 유효성 검증
        if (refreshToken != null && jwtTokenProvider.validate(refreshToken)) {
            // 3. 토큰에서 이메일 추출
            String email = jwtTokenProvider.getEmail(refreshToken);
            // 4. 이메일로 사용자 정보 조회
            User user = userRepository.findByEmail(email).orElse(null);
            // 5. 새로운 액세스 토큰 생성
            String newAccessToken = jwtTokenProvider.createToken(user.getEmail(), user.getId());
            // 6. 새로운 액세스 토큰 반환
            return ApiResponse.onSuccess(Map.of("accessToken", newAccessToken));
        }

        // 7. 유효하지 않은 토큰일 경우 에러 응답
        throw new BaseException(ErrorStatus.INVALID_OR_EXPIRED_TOKEN);
    }
}
