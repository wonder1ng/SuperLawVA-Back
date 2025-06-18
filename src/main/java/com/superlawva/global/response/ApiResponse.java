package com.superlawva.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.superlawva.global.response.status.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public static <T> ApiResponse<T> onSuccess(T data) {
        return new ApiResponse<>(true, SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(), data);
    }

    public static <T> ApiResponse<T> of(SuccessStatus status, T data) {
        return new ApiResponse<>(true, status.getCode(), status.getMessage(), data);
    }

    public static ApiResponse<Object> onFailure(String code, String message, Object data) {
        return new ApiResponse<>(false, code, message, data);
    }
} 