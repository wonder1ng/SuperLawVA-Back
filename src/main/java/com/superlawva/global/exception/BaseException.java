package com.superlawva.global.exception;

import com.superlawva.global.response.status.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
 
@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException {
    private final ErrorStatus errorStatus;
} 