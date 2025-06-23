package com.superlawva.global.response.status;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorStatus {

    // ✅ 공통 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // ✅ 회원 관련
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수 입니다."),

    // ✅ 게시글 관련
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ARTICLE4001", "게시글이 없습니다."),

    // ✅ 테스트용
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "이거는 테스트"),

    // ✅ JWT 관련
    INVALID_OR_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_OR_EXPIRED_TOKEN", "JWT가 유효하지 않거나 만료되었습니다."),

    // User
    _USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404", "존재하지 않는 사용자입니다."),
    _EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER409", "이미 가입된 이메일입니다."),
    _PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER400", "비밀번호가 일치하지 않습니다."),
    _PASSWORD_CONFIRM_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER400", "비밀번호와 비밀번호 확인이 일치하지 않습니다."),

    // Email Verification
    _VERIFICATION_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "EMAIL404", "인증 코드를 찾을 수 없습니다."),
    _VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "EMAIL400", "인증 코드가 만료되었습니다."),
    _VERIFICATION_CODE_NOT_MATCH(HttpStatus.BAD_REQUEST, "EMAIL400", "인증 코드가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
