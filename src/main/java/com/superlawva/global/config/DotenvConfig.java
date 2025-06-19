package com.superlawva.global.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class DotenvConfig {

    @PostConstruct
    public void loadEnv() {
        // .env 파일 로드 (로컬 개발용)
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        
        // .env 파일의 값들을 시스템 프로퍼티로 설정 (시스템 환경변수가 없는 경우에만)
        for (DotenvEntry entry : dotenv.entries()) {
            String key = entry.getKey();
            String envValue = System.getenv(key);
            
            // 시스템 환경변수가 없는 경우에만 .env 파일 값 사용
            if (envValue == null || envValue.isBlank()) {
                System.setProperty(key, entry.getValue());
            } else {
                // 시스템 환경변수를 시스템 프로퍼티로도 설정
                System.setProperty(key, envValue);
            }
        }
        
        // 주요 환경변수들을 직접 시스템 프로퍼티로 설정 (EC2 배포용)
        String[] requiredEnvVars = {
            "JWT_SECRET", "AES_SECRET_KEY", "DATABASE_URL", 
            "DB_USERNAME", "DB_PASSWORD", "MAIL_USERNAME", "MAIL_PASSWORD",
            "KAKAO_CLIENT_ID", "KAKAO_CLIENT_SECRET", 
            "NAVER_CLIENT_ID", "NAVER_CLIENT_SECRET"
        };
        
        for (String envVar : requiredEnvVars) {
            String value = System.getenv(envVar);
            if (value != null && !value.isBlank()) {
                System.setProperty(envVar, value);
            }
        }
    }
}
