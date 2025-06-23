package com.superlawva.global.security.converter;

import com.superlawva.global.security.util.AESUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Converter
@RequiredArgsConstructor
public class AesCryptoConverter implements AttributeConverter<String, String> {

    private final AESUtil aesUtil;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        // 엔티티의 속성(평문)을 DB 컬럼(암호문)으로 변환
        try {
            // AESUtil에서 null 처리하므로 바로 호출 가능
            return aesUtil.encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException("AES 암호화 실패: " + e.getMessage() + ", 입력값: " + attribute, e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        // DB 컬럼(암호문)을 엔티티의 속성(평문)으로 변환
        try {
            // AESUtil에서 null 처리하므로 바로 호출 가능
            return aesUtil.decrypt(dbData);
        } catch (Exception e) {
            throw new RuntimeException("AES 복호화 실패: " + e.getMessage() + ", 입력값: " + dbData, e);
        }
    }
} 