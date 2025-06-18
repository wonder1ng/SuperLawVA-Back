package com.superlawva.global.verification.service;

import com.superlawva.global.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final MailService mailService;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public void sendVerification(String email) {
        String code = generate6DigitCode();
        String key = "email:verify:" + email;

        // 1. Redis에 인증번호 저장 (30분 유효)
        redisTemplate.opsForValue().set(key, code, Duration.ofMinutes(30));

        // 2. HTML 형식 메일 본문 작성
        String subject = "이메일 인증번호";
        String body = makeMessageForm(code, "인증번호");

        try {
            mailService.sendEmail(email, subject, body);
        } catch (Exception e) {
            throw new RuntimeException("메일 전송 오류", e);
        }
    }

    public boolean verifyToken(String email, String code) {
        String key = "email:verify:" + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode != null && storedCode.equals(code)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    private String generate6DigitCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000~999999
        return String.valueOf(code);
    }

    private String makeMessageForm(String value, String purpose) {
        StringBuilder message = new StringBuilder();
        message
                .append("<h1 style='text-align: center;'>[SuperLawVA]</h1>")
                .append("<h3 style='text-align: center;'>")
                .append(purpose)
                .append(" : <strong style='font-size: 32px; letter-spacing: 8px;'>")
                .append(value)
                .append("</strong></h3>");
        return message.toString();
    }
}
