package com.superlawva.domain.alarm.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 알람 통계 정보 DTO
 */
@Getter
@Builder
public class AlarmStatsDTO {
    private Long userId;
    private int totalAlarms;
    private int unreadAlarms;
    private int urgentAlarms;
    private int todayAlarms;
    private int thisWeekAlarms;
    private int thisMonthAlarms;
} 