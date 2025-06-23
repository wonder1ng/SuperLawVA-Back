package com.superlawva;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackApplication {

    public static void main(String[] args) {
        // .env 파일을 찾아 시스템 프로퍼티로 로드합니다.
        // Spring Boot는 자동으로 시스템 프로퍼티를 읽어 설정에 사용합니다.
        Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .systemProperties()
                .load();

        SpringApplication.run(BackApplication.class, args);
    }
}
