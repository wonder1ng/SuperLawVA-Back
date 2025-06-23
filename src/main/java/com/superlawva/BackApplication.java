package com.superlawva;

import com.superlawva.domain.user.entity.User;
import com.superlawva.domain.user.repository.UserRepository;
import com.superlawva.global.security.util.HashUtil;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Bean
    @ConditionalOnProperty(name = "app.create-test-users", havingValue = "true", matchIfMissing = true)
    public CommandLineRunner createTestUsers(UserRepository userRepository, PasswordEncoder passwordEncoder, HashUtil hashUtil) {
        return args -> {
            try {
                System.out.println("🚀 테스트 사용자 생성 시작...");
                
                // 테스트용 사용자가 없으면 생성
                String testEmailHash = hashUtil.hash("test@example.com");
                if (!userRepository.existsByEmailHash(testEmailHash)) {
                    User testUser = User.builder()
                            .email("test@example.com")
                            .emailHash(testEmailHash)
                            .password(passwordEncoder.encode("password123"))
                            .name("테스트사용자")
                            .provider("LOCAL")
                            .role(User.Role.USER)
                            .emailVerified(true)
                            .build();
                    userRepository.save(testUser);
                    System.out.println("✅ 테스트 사용자 생성 완료: test@example.com / password123");
                } else {
                    System.out.println("ℹ️ 테스트 사용자 이미 존재: test@example.com");
                }

                String adminEmailHash = hashUtil.hash("admin@example.com");
                if (!userRepository.existsByEmailHash(adminEmailHash)) {
                    User adminUser = User.builder()
                            .email("admin@example.com")
                            .emailHash(adminEmailHash)
                            .password(passwordEncoder.encode("admin123"))
                            .name("관리자")
                            .provider("LOCAL")
                            .role(User.Role.ADMIN)
                            .emailVerified(true)
                            .build();
                    userRepository.save(adminUser);
                    System.out.println("✅ 관리자 사용자 생성 완료: admin@example.com / admin123");
                } else {
                    System.out.println("ℹ️ 관리자 사용자 이미 존재: admin@example.com");
                }

                String demoEmailHash = hashUtil.hash("demo@example.com");
                if (!userRepository.existsByEmailHash(demoEmailHash)) {
                    User demoUser = User.builder()
                            .email("demo@example.com")
                            .emailHash(demoEmailHash)
                            .password(passwordEncoder.encode("demo123"))
                            .name("데모사용자")
                            .provider("LOCAL")
                            .role(User.Role.USER)
                            .emailVerified(true)
                            .build();
                    userRepository.save(demoUser);
                    System.out.println("✅ 데모 사용자 생성 완료: demo@example.com / demo123");
                } else {
                    System.out.println("ℹ️ 데모 사용자 이미 존재: demo@example.com");
                }
                
                System.out.println("🎉 테스트 사용자 생성 과정 완료");
            } catch (Exception e) {
                System.err.println("⚠️ 테스트 사용자 생성 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
                // 에러가 발생해도 애플리케이션은 정상 시작되도록 함
            }
        };
    }
}
