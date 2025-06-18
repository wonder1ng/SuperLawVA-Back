package com.superlawva.global.response.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus {

    _OK(HttpStatus.OK, "200", "OK"),
    _CREATED(HttpStatus.CREATED, "201", "CREATED");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

} 