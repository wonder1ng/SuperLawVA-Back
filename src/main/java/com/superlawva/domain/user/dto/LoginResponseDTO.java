package com.superlawva.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LoginResponseDTO {
    private String message;
    private String userName;
    private String userId;
    private String JWTtoken;
    private List<Integer> notification;
    private ContractInfo contract;
    private List<RecentChat> recentChat;

    @Getter
    @Builder
    public static class ContractInfo {
        private String title;
        private String state;
        private String address;
        private String createdAt;
    }

    @Getter
    @Builder
    public static class RecentChat {
        private String _id;
        private String title;
    }
} 