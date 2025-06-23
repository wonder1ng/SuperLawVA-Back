package com.superlawva.domain.alarm.service;

import com.superlawva.domain.alarm.dto.AlarmDTO;
import com.superlawva.domain.alarm.service.AlarmService;
import com.superlawva.domain.alarm.service.ContractAlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlarmScheduler {
    private final AlarmService alarmService;
    private final ContractAlarmService contractAlarmService;

    @Scheduled(cron = "0 0 9 * * *") // 매일 오전 9시 실행
    public void generateContractAlarms() {
        log.info("Starting daily alarm generation");
        try {
            List<AlarmDTO> activeContracts = contractAlarmService.getActiveContracts();
            log.info("Found {} active contracts", activeContracts.size());

            for (AlarmDTO contract : activeContracts) {
                try {
                    alarmService.generateAlarmsForContract(contract);
                } catch (Exception e) {
                    log.error("Failed to generate alarms for contract: {}", contract.getId(), e);
                }
            }
            log.info("Completed daily alarm generation");
        } catch (Exception e) {
            log.error("Failed to generate daily alarms", e);
        }
    }

    @Scheduled(cron = "0 0 * * * *") // 매시간 정각 실행
    public void processPendingAlarms() {
        log.info("Processing pending alarms");
        // TODO: 실제 알람 발송 로직 구현 (푸시 알림, 이메일, SMS 등)
    }
} 