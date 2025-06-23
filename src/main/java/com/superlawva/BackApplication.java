package com.superlawva;

import com.superlawva.domain.user.entity.User;
import com.superlawva.domain.user.repository.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
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

    @Bean
    public CommandLineRunner createTestUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 테스트용 사용자가 없으면 생성
            if (!userRepository.existsByEmail("test@example.com")) {
                User testUser = User.builder()
                        .email("test@example.com")
                        .password(passwordEncoder.encode("password123"))
                        .name("테스트사용자")
                        .provider("LOCAL")
                        .role(User.Role.USER)
                        .emailVerified(true)
                        .build();
                userRepository.save(testUser);
                System.out.println("✅ 테스트 사용자 생성 완료: test@example.com / password123");
            }

            if (!userRepository.existsByEmail("admin@example.com")) {
                User adminUser = User.builder()
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("admin123"))
                        .name("관리자")
                        .provider("LOCAL")
                        .role(User.Role.ADMIN)
                        .emailVerified(true)
                        .build();
                userRepository.save(adminUser);
                System.out.println("✅ 관리자 사용자 생성 완료: admin@example.com / admin123");
            }

            if (!userRepository.existsByEmail("demo@example.com")) {
                User demoUser = User.builder()
                        .email("demo@example.com")
                        .password(passwordEncoder.encode("demo123"))
                        .name("데모사용자")
                        .provider("LOCAL")
                        .role(User.Role.USER)
                        .emailVerified(true)
                        .build();
                userRepository.save(demoUser);
                System.out.println("✅ 데모 사용자 생성 완료: demo@example.com / demo123");
            }
        };
    }
}
