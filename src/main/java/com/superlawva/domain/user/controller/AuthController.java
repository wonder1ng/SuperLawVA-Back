package com.superlawva.domain.user.controller;

import com.superlawva.domain.user.dto.KakaoLoginRequestDTO;
import com.superlawva.domain.user.dto.LoginRequestDTO;
import com.superlawva.domain.user.dto.LoginResponseDTO;
import com.superlawva.domain.user.dto.NaverLoginRequestDTO;
import com.superlawva.domain.user.dto.UserRequestDTO;
import com.superlawva.domain.user.dto.UserResponseDTO;
import com.superlawva.domain.user.service.UserService;
import com.superlawva.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "인증 (회원가입, 로그인)")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "일반 회원가입", description = "이메일과 비밀번호를 사용하여 새로운 사용자를 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 (유효성 검사 실패)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 존재하는 이메일")
    })
    public ApiResponse<UserResponseDTO> signUp(@RequestBody @Valid UserRequestDTO.SignUpDTO request) {
        return ApiResponse.onSuccess(userService.signUp(request));
    }

    @PostMapping("/login")
    @Operation(summary = "일반 로그인", description = "이메일과 비밀번호로 로그인하고 JWT 토큰을 발급받습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패 (이메일 또는 비밀번호 불일치)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 사용자")
    })
    public ApiResponse<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        return ApiResponse.onSuccess(userService.login(request));
    }

    @PostMapping("/login/kakao")
    @Operation(summary = "카카오 소셜 로그인", description = "카카오 인가 코드를 받아 로그인 처리 후 JWT 토큰을 발급합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 인가 코드"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "카카오 서버 통신 오류")
    })
    public ApiResponse<LoginResponseDTO> kakaoLogin(@RequestBody @Valid KakaoLoginRequestDTO request) {
        return ApiResponse.onSuccess(userService.kakaoLogin(request));
    }

    @PostMapping("/login/naver")
    @Operation(summary = "네이버 소셜 로그인", description = "네이버 인가 코드를 받아 로그인 처리 후 JWT 토큰을 발급합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 인가 코드 또는 state"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "네이버 서버 통신 오류")
    })
    public ApiResponse<LoginResponseDTO> naverLogin(@RequestBody @Valid NaverLoginRequestDTO request) {
        return ApiResponse.onSuccess(userService.naverLogin(request));
    }
} 