package com.superlawva.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "로그인 응답 DTO")
public class LoginResponseDTO {

    @Schema(description = "JWT Access Token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIiwiaWF0IjoxNzE5MjA5ODg5LCJleHAiOjE3MTkyMTM0ODl9.abcdef123456")
    private String accessToken;

    @JsonProperty("userInfo")
    @Schema(description = "로그인한 사용자 정보")
    private UserInfoDTO userInfo;


    @Getter
    @Builder
    @Schema(description = "로그인한 사용자 상세 정보")
    public static class UserInfoDTO {
        @Schema(description = "사용자 ID (이메일)", example = "test@superlaw.com")
        private String email;
        @Schema(description = "사용자 닉네임", example = "슈퍼로바")
        private String nickname;
        
        // 아래 필드들은 프론트엔드 UI 구성을 위한 샘플 데이터입니다.
        @Schema(description = "알림 정보 (샘플 데이터)", example = "[1, 2, 3]")
        private List<Integer> notification;
        @Schema(description = "계약 정보 (샘플 데이터)")
        private ContractInfo contract;
        @Schema(description = "최근 채팅 정보 (샘플 데이터)")
        private List<RecentChat> recentChat;
    }


    @Getter
    @Builder
    @Schema(description = "계약 정보 (샘플)")
    public static class ContractInfo {
        @Schema(description = "계약명", example = "근로 계약서")
        private String title;
        @Schema(description = "계약 상태", example = "진행중")
        private String state;
        @Schema(description = "주소", example = "서울시 강남구")
        private String address;
        @Schema(description = "생성일", example = "2023-06-23")
        private String createdAt;
    }

    @Getter
    @Builder
    @Schema(description = "최근 채팅 정보 (샘플)")
    public static class RecentChat {
        @JsonProperty("_id")
        @Schema(description = "채팅방 ID", example = "chat-12345")
        private String _id;
        @Schema(description = "채팅방 제목", example = "AI 변호사 챗봇")
        private String title;
    }
} 