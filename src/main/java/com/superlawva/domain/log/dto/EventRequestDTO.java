package com.superlawva.domain.log.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(
        description = "이벤트 요청 DTO",
        example = """
                {
                  "type": "click",
                  "target": "button.submit",
                  "time": "2025-06-05T12:00:00Z",
                  "sessionId": 1,
                  "viewId": 1,
                  "userId": 1,
                  "meta": {
                    "elementId": "main-login-button",
                    "x": 120,
                    "y": 250
                  }
                }
                """
)
public record EventRequestDTO(

        @Schema(description = "이벤트 종류 (예: click, input, hover, scroll)", example = "click") String type,
        @Schema(description = "이벤트 대상 식별자", example = "button.submit") String target,
        @Schema(description = "이벤트 발생 시간", example = "2025-06-05T12:00:00Z") LocalDateTime time,
        @Schema(description = "세션 ID") Long sessionId,
        @Schema(description = "페이지뷰 ID") Long viewId,
        @Schema(description = "사용자 ID (비로그인 시 null 가능)", nullable = true) Long userId,
        @Schema(
                description = "이벤트 유형에 따른 상세 데이터",
                example = """
                        {
                          "elementId": "main-login-button",
                          "x": 120,
                          "y": 250
                        }
                        """
        )
        Map<String, Object> meta
) {}
