package com.superlawva.domain.user.repository;

import com.superlawva.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailHash(String emailHash);
    boolean existsByEmailHash(String emailHash);
    Optional<User> findByKakaoId(Long kakaoId);
    Optional<User> findByNaverId(String naverId);
}
