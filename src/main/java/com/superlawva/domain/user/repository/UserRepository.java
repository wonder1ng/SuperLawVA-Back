package com.superlawva.domain.user.repository;

import com.superlawva.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByKakaoId(Long kakaoId);
}
