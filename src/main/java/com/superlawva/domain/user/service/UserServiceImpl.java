package com.superlawva.domain.user.service;

import com.superlawva.domain.user.dto.*;
import com.superlawva.domain.user.entity.User;
import com.superlawva.domain.user.repository.UserRepository;
import com.superlawva.global.exception.BaseException;
import com.superlawva.global.response.status.ErrorStatus;
import com.superlawva.global.security.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public void register(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new BaseException(ErrorStatus._EMAIL_ALREADY_EXISTS);
        }
        String hashedPassword = passwordEncoder.encode(userRequestDTO.getPassword());
        User user = User.builder()
                .email(userRequestDTO.getEmail())
                .password(hashedPassword)
                .name(userRequestDTO.getName())
                .provider("LOCAL")
                .role(User.Role.USER)
                .build();
        userRepository.save(user);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new BaseException(ErrorStatus.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new BaseException(ErrorStatus._PASSWORD_NOT_MATCH);
        }

        String token = jwtTokenProvider.createToken(user.getEmail(), user.getId());
        return LoginResponseDTO.builder()
                .token(token)
                .email(user.getEmail())
                .nickname(user.getName())
                .provider(user.getProvider())
                .build();
    }

    @Override
    @Transactional
    public LoginResponseDTO kakaoLogin(KakaoLoginRequestDTO kakaoLoginRequestDTO) {
        User user = userRepository.findByEmail(kakaoLoginRequestDTO.getEmail())
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(kakaoLoginRequestDTO.getEmail())
                        .name(kakaoLoginRequestDTO.getNickname())
                        .provider("KAKAO")
                        .role(User.Role.USER)
                        .emailVerified(true)
                        .build()));

        String token = jwtTokenProvider.createToken(user.getEmail(), user.getId());
        return LoginResponseDTO.builder()
                .token(token)
                .email(user.getEmail())
                .nickname(user.getName())
                .provider(user.getProvider())
                .build();
    }

    @Override
    @Transactional
    public LoginResponseDTO naverLogin(NaverLoginRequestDTO naverLoginRequestDTO) {
        User user = userRepository.findByEmail(naverLoginRequestDTO.getEmail())
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(naverLoginRequestDTO.getEmail())
                        .name(naverLoginRequestDTO.getName())
                        .provider("NAVER")
                        .role(User.Role.USER)
                        .emailVerified(true)
                        .build()));

        String token = jwtTokenProvider.createToken(user.getEmail(), user.getId());
        return LoginResponseDTO.builder()
                .token(token)
                .email(user.getEmail())
                .nickname(user.getName())
                .provider(user.getProvider())
                .build();
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(ErrorStatus.MEMBER_NOT_FOUND));
    }

    @Override
    @Transactional
    public void changePassword(User user, PasswordChangeRequestDTO request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BaseException(ErrorStatus._PASSWORD_NOT_MATCH);
        }
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new BaseException(ErrorStatus._PASSWORD_CONFIRM_NOT_MATCH);
        }
        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
