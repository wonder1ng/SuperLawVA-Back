package com.superlawva;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BackApplication {
    public static void main(String[] args) {
        // .env 파일 로드
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .filename(".env")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        if (dotenv.get("DATABASE_URL") == null) {
            throw new IllegalStateException(".env 파일이 제대로 로드되지 않았습니다.");
        }

        // Spring Boot에서 인식할 수 있는 프로퍼티로 매핑
        Map<String, Object> env = new HashMap<>();
        env.put("spring.datasource.url", dotenv.get("DATABASE_URL"));
        env.put("spring.datasource.username", dotenv.get("DB_USERNAME"));
        env.put("spring.datasource.password", dotenv.get("DB_PASSWORD"));
        env.put("spring.data.redis.host", dotenv.get("REDIS_HOST"));
        env.put("spring.data.redis.port", dotenv.get("REDIS_PORT"));

        env.put("spring.mail.username", dotenv.get("MAIL_USERNAME"));
        env.put("spring.mail.password", dotenv.get("MAIL_PASSWORD"));

        env.put("spring.security.oauth2.client.registration.kakao.client-id", dotenv.get("KAKAO_CLIENT_ID"));
        env.put("spring.security.oauth2.client.registration.kakao.client-secret", dotenv.get("KAKAO_CLIENT_SECRET"));
        env.put("spring.security.oauth2.client.registration.naver.client-id", dotenv.get("NAVER_CLIENT_ID"));
        env.put("spring.security.oauth2.client.registration.naver.client-secret", dotenv.get("NAVER_CLIENT_SECRET"));

        env.put("jwt.secret", dotenv.get("JWT_SECRET"));
        env.put("jwt.access-token-validity", dotenv.get("JWT_ACCESS_TOKEN_VALIDITY"));

        env.put("frontend.url", dotenv.get("FRONTEND_URL"));
        env.put("server.port", dotenv.get("SERVER_PORT", "8080"));

        SpringApplication app = new SpringApplication(BackApplication.class);
        app.setDefaultProperties(env);
        app.run(args);

        System.out.println("✅ Loaded DB URL: " + dotenv.get("DATABASE_URL"));

    }

}
