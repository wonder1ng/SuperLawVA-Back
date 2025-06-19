package com.superlawva.domain.user.controller;

import com.superlawva.domain.user.dto.UserRequestDTO;
import com.superlawva.domain.user.dto.UserResponseDTO;
import com.superlawva.domain.user.dto.PasswordChangeRequestDTO;
import com.superlawva.domain.user.entity.User;
import com.superlawva.domain.user.service.UserService;
import com.superlawva.global.response.ApiResponse;
import com.superlawva.global.response.status.ErrorStatus;
import com.superlawva.global.security.annotation.LoginUser;
import com.superlawva.global.exception.BaseException;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Users", description = "유저 관리")
public class UserController {

    private final UserService userService;

    @Operation(summary = "전체 회원 조회", description = "등록된 모든 회원 정보를 조회합니다.")
    @GetMapping
    public List<UserResponseDTO> all() {
        return userService.findAll();
    }

    @Operation(summary = "회원 조회", description = "ID를 이용하여 특정 회원 정보를 조회합니다.")
    @GetMapping("/{id}")
    public UserResponseDTO one(@PathVariable Long id) {
        return userService.findById(id);
    }

    @Operation(summary = "회원 등록", description = "새로운 회원 정보를 등록합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO create(@RequestBody UserRequestDTO dto) {
        return userService.create(dto);
    }

    @Operation(summary = "회원 수정", description = "ID에 해당하는 회원 정보를 수정합니다.")
    @PutMapping("/{id}")
    public UserResponseDTO update(@PathVariable Long id,
                                  @RequestBody UserRequestDTO dto) {
        return userService.update(id, dto);
    }

    @Operation(summary = "회원 삭제", description = "ID에 해당하는 회원 정보를 삭제합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @GetMapping("/info")
    @Operation(summary = "유저 정보 조회", description = "로그인한 유저의 정보를 조회합니다.")
    public ApiResponse<UserResponseDTO> getMyInfo(@LoginUser User user) {
        if (user == null) {
            throw new BaseException(ErrorStatus._UNAUTHORIZED);
        }
        return ApiResponse.onSuccess(userService.getMyInfo(user.getEmail()));
    }

    @PutMapping("/password")
    @Operation(summary = "비밀번호 변경", description = "로그인한 유저의 비밀번호를 변경합니다.")
    public ApiResponse<Void> changePassword(@LoginUser User user, @RequestBody @Valid PasswordChangeRequestDTO request) {
        if (user == null) {
            throw new BaseException(ErrorStatus._UNAUTHORIZED);
        }
        userService.changePassword(user.getEmail(), request);
        return ApiResponse.onSuccess(null);
    }
}
