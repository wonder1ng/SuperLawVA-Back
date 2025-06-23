package com.superlawva.domain.user.controller;

import com.superlawva.domain.user.dto.UserRequestDTO;
import com.superlawva.domain.user.dto.UserResponseDTO;
import com.superlawva.domain.user.dto.LoginResponseDTO;
import com.superlawva.domain.user.dto.PasswordChangeRequestDTO;
import com.superlawva.domain.user.entity.User;
import com.superlawva.domain.user.service.UserService;
import com.superlawva.global.response.ApiResponse;
import com.superlawva.global.response.status.ErrorStatus;
import com.superlawva.global.security.annotation.LoginUser;
import com.superlawva.global.exception.BaseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User", description = "사용자 정보 조회 및 관리 (🔒인증 필요)")
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    @Operation(summary = "내 정보 조회 (마이페이지)", description = "현재 로그인한 사용자의 상세 정보를 조회합니다.")
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "정보 조회 성공", content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음")
    })
    public ApiResponse<UserResponseDTO> getMyInfo(@Parameter(hidden = true) @LoginUser User user) {
        if (user == null) {
            throw new BaseException(ErrorStatus._UNAUTHORIZED);
        }
        return ApiResponse.onSuccess(userService.getMyInfo(user.getId()));
    }

    @PostMapping("/dashboard")
    @Operation(summary = "사용자 대시보드 정보 조회", description = "프론트엔드 호환을 위한 사용자 대시보드 정보를 조회합니다.")
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "대시보드 정보 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    public ApiResponse<LoginResponseDTO> getUserDashboard(@Parameter(hidden = true) @LoginUser User user) {
        if (user == null) {
            throw new BaseException(ErrorStatus._UNAUTHORIZED);
        }
        return ApiResponse.onSuccess(userService.getUserDashboard(user.getId()));
    }

    @PutMapping("/info")
    @Operation(summary = "내 정보 수정", description = "현재 로그인한 사용자의 닉네임 등 정보를 수정합니다.")
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "정보 수정 성공", content = @Content(schema = @Schema(implementation = UserResponseDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음")
    })
    public ApiResponse<UserResponseDTO> updateMyInfo(@Parameter(hidden = true) @LoginUser User user, @RequestBody @Valid UserRequestDTO.UpdateMyInfoDTO request) {
        if (user == null) {
            throw new BaseException(ErrorStatus._UNAUTHORIZED);
        }
        return ApiResponse.onSuccess(userService.updateMyInfo(user.getId(), request));
    }

    @DeleteMapping("/me")
    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 계정을 비활성화(삭제) 처리합니다.")
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음")
    })
    public ApiResponse<Void> deleteMyAccount(@Parameter(hidden = true) @LoginUser User user) {
        if (user == null) {
            throw new BaseException(ErrorStatus._UNAUTHORIZED);
        }
        userService.deleteMyAccount(user.getId());
        return ApiResponse.onSuccess(null);
    }

    @PatchMapping("/me/password")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "내 비밀번호 변경", description = "로그인한 사용자의 비밀번호를 변경합니다.")
    public ApiResponse<String> changePassword(@Parameter(hidden = true) @LoginUser User user,
                                              @RequestBody @Valid PasswordChangeRequestDTO request) {
        userService.changePassword(user, request);
        return ApiResponse.onSuccess("비밀번호가 성공적으로 변경되었습니다.");
    }
    
    // ================================================================================================================
    // 아래 API들은 관리자(ADMIN) 권한이 필요하며, 현재는 데모 및 테스트 목적으로 열려있습니다.
    // 실제 운영 시에는 @PreAuthorize("hasRole('ADMIN')") 등을 사용하여 접근을 제한해야 합니다.
    // ================================================================================================================

    @Operation(summary = "전체 회원 조회 (관리자용)", description = "등록된 모든 회원 정보를 조회합니다.")
    @GetMapping
    @SecurityRequirement(name = "JWT")
    public ApiResponse<List<UserResponseDTO>> all() {
        return ApiResponse.onSuccess(userService.findAll());
    }

    @Operation(summary = "특정 회원 조회 (관리자용)", description = "ID를 이용하여 특정 회원 정보를 조회합니다.")
    @GetMapping("/{id}")
    @SecurityRequirement(name = "JWT")
    public ApiResponse<UserResponseDTO> one(@Parameter(description = "사용자 ID", in = ParameterIn.PATH) @PathVariable Long id) {
        return ApiResponse.onSuccess(userService.findById(id));
    }

    @Operation(summary = "회원 정보 수정 (관리자용)", description = "ID에 해당하는 회원 정보를 수정합니다.")
    @PutMapping("/{id}")
    @SecurityRequirement(name = "JWT")
    public ApiResponse<UserResponseDTO> update(@Parameter(description = "사용자 ID", in = ParameterIn.PATH) @PathVariable Long id,
                                  @RequestBody UserRequestDTO dto) {
        return ApiResponse.onSuccess(userService.update(id, dto));
    }

    @Operation(summary = "회원 삭제 (관리자용)", description = "ID에 해당하는 회원 정보를 삭제합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "JWT")
    public void delete(@Parameter(description = "사용자 ID", in = ParameterIn.PATH) @PathVariable Long id) {
        userService.delete(id);
    }
}
