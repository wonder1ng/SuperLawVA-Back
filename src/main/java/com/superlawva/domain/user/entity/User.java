package com.superlawva.domain.user.entity;

import com.superlawva.global.security.converter.AesCryptoConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(columnList = "kakaoId", unique = true),
                @Index(columnList = "naverId", unique = true),
                @Index(columnList = "emailHash", unique = true)
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 소셜 로그인(카카오) 시에만 값 존재 */
    @Column(unique = true)
    private Long kakaoId;

    /** 소셜 로그인(네이버) 시에만 값 존재 */
    @Column(unique = true)
    private String naverId;

    @Column(nullable = false, unique = true)
    private String emailHash;

    @Column(nullable = false)
    @Convert(converter = AesCryptoConverter.class)
    private String email;

    @Column(nullable = true)  // 소셜 로그인 사용자는 password가 null일 수 있음
    private String password;

    @Column(nullable = false)
    @Convert(converter = AesCryptoConverter.class)
    private String name;

    @Column(nullable = false)
    private String provider; // LOCAL, KAKAO, NAVER

    /** USER · ADMIN */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;
    public enum Role { USER, ADMIN }

    /** 생성-수정 시각 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /** 이메일 인증 여부 */
    @Builder.Default
    @Column(nullable = false)
    private boolean emailVerified = false;

    /* ==================== JPA Life-cycle ==================== */

       @PrePersist
    protected void prePersist() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /* ==================== 도메인 로직 ==================== */

    public void changeEmail(String email) {
        this.email = email;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public void updateSnsInfo(String name, String provider) {
        this.name = name;
        this.provider = provider;
    }
}

