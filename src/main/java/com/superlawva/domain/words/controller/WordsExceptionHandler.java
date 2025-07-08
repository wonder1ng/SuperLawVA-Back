<<<<<<< HEAD
// WordsExceptionHandler.java
=======
>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
package com.superlawva.domain.words.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.superlawva.domain.words")
@Slf4j
public class WordsExceptionHandler {
<<<<<<< HEAD
    
=======

>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
    /**
     * Validation 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
<<<<<<< HEAD
        
=======

>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
<<<<<<< HEAD
        
        log.warn("Validation 오류 발생: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    
=======

        log.warn("[WordsExceptionHandler] Validation 오류 발생: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
    /**
     * Constraint Violation 예외 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(
            ConstraintViolationException ex) {
<<<<<<< HEAD
        
=======

>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });
<<<<<<< HEAD
        
        log.warn("Constraint Violation 오류 발생: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    
=======

        log.warn("[WordsExceptionHandler] Constraint Violation 오류 발생: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
    /**
     * 일반 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "서버 내부 오류가 발생했습니다.");
        error.put("message", ex.getMessage());
<<<<<<< HEAD
        
        log.error("예상치 못한 오류 발생", ex);
=======

        log.error("[WordsExceptionHandler] 예상치 못한 오류 발생", ex);
>>>>>>> 2e0a7457de52b2c07313d113e115aa4a044a6be3
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}