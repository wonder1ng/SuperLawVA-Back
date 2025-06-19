package com.superlawva;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BackApplication {
    public static void main(String[] args) {
        // .env 파일 로드 (.env 또는 환경변수)
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .filename(".env")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        // 운영: System.getenv(), 개발: dotenv.get() → 우선순위는 운영
        Map<String, Object> env = new HashMap<>();
        env.put("spring.datasource.url", getEnv("DATABASE_URL", dotenv));
        env.put("spring.datasource.username", getEnv("DB_USERNAME", dotenv));
        env.put("spring.datasource.password", getEnv("DB_PASSWORD", dotenv));

        env.put("spring.data.redis.host", getEnv("REDIS_HOST", dotenv));
        env.put("spring.data.redis.port", getEnv("REDIS_PORT", dotenv));

        env.put("spring.mail.username", getEnv("MAIL_USERNAME", dotenv));
        env.put("spring.mail.password", getEnv("MAIL_PASSWORD", dotenv));

        env.put("spring.security.oauth2.client.registration.kakao.client-id", getEnv("KAKAO_CLIENT_ID", dotenv));
        env.put("spring.security.oauth2.client.registration.kakao.client-secret", getEnv("KAKAO_CLIENT_SECRET", dotenv));
        env.put("spring.security.oauth2.client.registration.naver.client-id", getEnv("NAVER_CLIENT_ID", dotenv));
        env.put("spring.security.oauth2.client.registration.naver.client-secret", getEnv("NAVER_CLIENT_SECRET", dotenv));

        env.put("jwt.secret", getEnv("JWT_SECRET", dotenv));
        env.put("jwt.access-token-validity", getEnv("JWT_ACCESS_TOKEN_VALIDITY", dotenv));

        env.put("frontend.url", getEnv("FRONTEND_URL", dotenv));
        env.put("server.port", getEnv("SERVER_PORT", dotenv));

        SpringApplication app = new SpringApplication(BackApplication.class);
        app.setDefaultProperties(env);
        app.run(args);

        System.out.println("✅ Loaded DB URL: " + env.get("spring.datasource.url"));
    }

    private static String getEnv(String key, Dotenv dotenv) {
        String sysValue = System.getenv(key);
        return (sysValue != null && !sysValue.isBlank()) ? sysValue : dotenv.get(key);
    }
}
