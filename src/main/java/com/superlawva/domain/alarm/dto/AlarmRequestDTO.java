package com.superlawva.domain.alarm.dto;

import com.superlawva.domain.alarm.entity.AlarmType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmRequestDTO {
    private Long userId;
    private String contractId;
    private AlarmType alarmType;
    private String extraInfo;
    private LocalDateTime alarmDate;
} 