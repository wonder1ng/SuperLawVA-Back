// EmailVerifyRequestDTO.java
package com.superlawva.global.verification.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailVerifyRequestDTO {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 잘못되었습니다.")
    private String email;

    @NotBlank(message = "인증번호는 필수입니다.")
    private String code;
}
