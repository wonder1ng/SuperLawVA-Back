package com.superlawva.global.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-validity:43200000}") // 기본 12시간
    private long validityMs;

    private SecretKey key;

    @PostConstruct
    public void initialize() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("❌ jwt.secret is missing. Check environment variables or .env file.");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String email, Long userId) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validityMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Long getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Long.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validate(String token) {
        return validateToken(token);
    }

    public String getEmail(String token) {
        return getSubject(token);
    }

    public String resolveToken(jakarta.servlet.http.HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String createToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validityMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 소셜 로그인 임시 토큰 생성 (이메일 정보가 없을 때 사용)
     * @param socialId 소셜 플랫폼 사용자 ID
     * @param provider 소셜 플랫폼 (KAKAO, NAVER)
     * @param nickname 닉네임
     * @return 임시 토큰 (짧은 유효기간)
     */
    public String createTempToken(String socialId, String provider, String nickname) {
        Claims claims = Jwts.claims().setSubject("TEMP");
        claims.put("socialId", socialId);
        claims.put("provider", provider);
        claims.put("nickname", nickname);
        claims.put("type", "TEMP");

        // 임시 토큰은 30분만 유효
        long tempValidityMs = 30 * 60 * 1000; // 30분

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tempValidityMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 임시 토큰에서 소셜 정보 추출
     * @param tempToken 임시 토큰
     * @return 소셜 정보 맵
     */
    public Claims getTempTokenClaims(String tempToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(tempToken)
                    .getBody();
            
            // 임시 토큰인지 확인
            if (!"TEMP".equals(claims.getSubject()) || !"TEMP".equals(claims.get("type"))) {
                throw new IllegalArgumentException("유효하지 않은 임시 토큰입니다.");
            }
            
            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            throw new IllegalArgumentException("임시 토큰이 유효하지 않거나 만료되었습니다.");
        }
    }
}
