package com.superlawva.global.exception.handler;

import com.superlawva.global.exception.BaseException;
import com.superlawva.global.response.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<Object> handleBaseException(BaseException e, WebRequest request) {
        return handleExceptionInternal(e, e.getErrorStatus(), new HttpHeaders(), e.getErrorStatus().getHttpStatus(), request);
    }

    private ResponseEntity<Object> handleExceptionInternal(Exception e, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiResponse<Object> responseBody = ApiResponse.onFailure(String.valueOf(status.value()), body.toString(), null);
        return super.handleExceptionInternal(e, responseBody, headers, status, request);
    }
} 