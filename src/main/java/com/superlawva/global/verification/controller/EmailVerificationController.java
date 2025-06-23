package com.superlawva.global.verification.controller;

import com.superlawva.global.response.ApiResponse;
import com.superlawva.global.verification.dto.request.EmailSendRequestDTO;
import com.superlawva.global.verification.dto.request.EmailVerifyRequestDTO;
import com.superlawva.global.verification.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Tag(name = "Email Verification", description = "이메일 인증")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send")
    @Operation(summary = "인증코드 이메일 발송", description = "가입하려는 이메일로 6자리 인증코드를 발송합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "인증코드 발송 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 가입된 이메일")
    })
    public ApiResponse<Void> sendVerificationEmail(@RequestBody @Valid EmailSendRequestDTO request) {
        emailVerificationService.sendVerificationEmail(request.getEmail());
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/verify")
    @Operation(summary = "인증코드 확인", description = "이메일과 발송된 인증코드를 확인하여 이메일 소유권을 검증합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이메일 인증 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "인증 코드가 일치하지 않거나 만료됨"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "인증 코드를 찾을 수 없음 (이메일 발송을 먼저 수행해야 함)")
    })
    public ApiResponse<Void> verifyEmail(@RequestBody @Valid EmailVerifyRequestDTO request) {
        emailVerificationService.verifyEmail(request);
        return ApiResponse.onSuccess(null);
    }
} 