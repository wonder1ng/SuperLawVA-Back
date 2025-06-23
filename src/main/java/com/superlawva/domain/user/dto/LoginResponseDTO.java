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
    private String token;

    @Schema(description = "사용자 닉네임", example = "아무개")
    private String userName;

    @Schema(description = "알림 정보 (샘플 데이터)", example = "[0, 1, 2]")
    private List<Integer> notification;

    @Schema(description = "계약 정보 목록 (샘플 데이터)")
    private List<ContractInfo> contractArray;

    @Schema(description = "최근 채팅 정보 목록 (샘플 데이터)")
    private List<RecentChat> recentChat;


    @Getter
    @Builder
    @Schema(description = "계약 정보")
    public static class ContractInfo {
        @JsonProperty("_id")
        @Schema(description = "계약 ID", example = "asdasd")
        private String _id;
        @Schema(description = "계약명", example = "월세 임대차 계약서")
        private String title;
        @Schema(description = "계약 상태", example = "진행중")
        private String state;
        @Schema(description = "주소", example = "서울시 강남구 테헤란로 123")
        private String address;
        @Schema(description = "생성일", example = "2025.03.22")
        private String createdAt;
    }

    @Getter
    @Builder
    @Schema(description = "최근 채팅 정보")
    public static class RecentChat {
        @JsonProperty("_id")
        @Schema(description = "채팅방 ID", example = "1")
        private String _id;
        @Schema(description = "채팅방 제목", example = "집 주인이 보증금 안 돌려줘요.")
        private String title;
    }
} 