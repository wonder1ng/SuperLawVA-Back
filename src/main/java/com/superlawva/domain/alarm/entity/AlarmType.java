package com.superlawva.domain.alarm.entity;

import java.util.Map;

public enum AlarmType {
    묵시갱신_시작,
    자동연장,
    종료예정,
    월세납부,
    관리비납부,
    중도금납부,
    잔금납부,
    입주예정,
    계약서생성;

    private static final Map<AlarmType, String> noteMessageMap = Map.of(
            묵시갱신_시작, "계약 만료 6개월 전입니다. 묵시적 갱신 여부를 고려해보세요.",
            자동연장, "계약 만료 2개월 전입니다. 자동 연장이 예정되어 있습니다.",
            종료예정, "계약 만료 1개월 전입니다. 계약 종료를 준비하세요.",
            월세납부, "월세 납부일이 다가오고 있습니다.",
            관리비납부, "관리비 납부일이 곧 도래합니다.",
            중도금납부, "중도금 납부 예정일이 다가옵니다.",
            잔금납부, "잔금 납부 예정일이 다가옵니다.",
            입주예정, "입주 예정일이 다가옵니다. 준비사항을 점검하세요.",
            계약서생성, "계약서가 생성되었습니다. 내용을 확인하세요."
    );

    public String getDefaultNote() {
        return noteMessageMap.getOrDefault(this, "알림이 도착했습니다.");
    }
} 