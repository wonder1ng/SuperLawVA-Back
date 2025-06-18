package com.superlawva.global.security.service;

import com.superlawva.domain.user.entity.User;
import com.superlawva.domain.user.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepo;

    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음"));
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword())
                .roles("USER")
                .build();
    }
}
