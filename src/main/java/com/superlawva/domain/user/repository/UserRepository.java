package com.superlawva.domain.user.repository;

import com.superlawva.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByNicknameContaining(String nickname);
    boolean existsByEmail(String email);
    Optional<User> findByKakaoId(Long kakaoId);
    Optional<User> findByNaverId(String naverId);
}
