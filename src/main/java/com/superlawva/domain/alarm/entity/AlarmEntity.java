package com.superlawva.domain.alarm.entity;

import com.superlawva.domain.alarm.entity.AlarmType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contract_alarm")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alarmId;
    
    private Long userId;
    
    @Column(name = "contract_id")
    private String contractId;
    
    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;
    
    private String note;
    
    @Column(columnDefinition = "TEXT")
    private String extraInfo;
    
    private boolean isRead;
    
    private LocalDateTime alarmDate;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime deletedAt;
    
    private boolean isSent;
    
    private LocalDateTime sentAt;
} 