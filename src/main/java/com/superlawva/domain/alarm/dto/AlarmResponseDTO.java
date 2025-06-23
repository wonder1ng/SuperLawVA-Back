package com.superlawva.domain.alarm.dto;

import com.superlawva.domain.alarm.entity.AlarmType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmResponseDTO {
    private Long alarmId;
    private String contractId;
    private AlarmType alarmType;
    private String note;
    private String extraInfo;
    private boolean isRead;
    private LocalDateTime alarmDate;
    private LocalDateTime createdAt;
} 