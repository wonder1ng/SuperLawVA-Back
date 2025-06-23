package com.superlawva.domain.alarm.repository;

import com.superlawva.domain.alarm.entity.AlarmEntity;
import com.superlawva.domain.alarm.entity.AlarmType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {
    List<AlarmEntity> findByUserIdAndIsReadFalseAndDeletedAtIsNull(Long userId);
    
    boolean existsByContractIdAndAlarmTypeAndDeletedAtIsNull(String contractId, AlarmType alarmType);
    
    @Query("SELECT ca FROM AlarmEntity ca WHERE ca.alarmDate <= :now AND ca.isSent = false AND ca.deletedAt IS NULL")
    List<AlarmEntity> findAlarmsToSend(@Param("now") LocalDateTime now);
    
    @Query("SELECT ca FROM AlarmEntity ca WHERE ca.alarmDate BETWEEN :start AND :end AND ca.deletedAt IS NULL")
    List<AlarmEntity> findAlarmsByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // 사용자별 전체 알람 조회 (삭제되지 않은 것만)
    List<AlarmEntity> findByUserIdAndDeletedAtIsNull(Long userId);
    
    // 사용자별 읽지 않은 알람 개수
    long countByUserIdAndIsReadFalseAndDeletedAtIsNull(Long userId);
    
    // 사용자별 전체 알람 개수
    long countByUserIdAndDeletedAtIsNull(Long userId);
    
    // 사용자별 긴급 알람 개수
    @Query("SELECT COUNT(ca) FROM AlarmEntity ca WHERE ca.userId = :userId AND ca.alarmType IN (:urgentTypes) AND ca.deletedAt IS NULL")
    long countUrgentAlarmsByUserId(@Param("userId") Long userId, @Param("urgentTypes") List<AlarmType> urgentTypes);
} 