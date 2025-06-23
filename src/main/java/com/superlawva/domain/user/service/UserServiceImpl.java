package com.superlawva.domain.user.service;

import com.superlawva.domain.user.dto.*;
import com.superlawva.domain.user.entity.User;
import com.superlawva.domain.user.repository.UserRepository;
import com.superlawva.global.exception.BaseException;
import com.superlawva.global.response.status.ErrorStatus;
import com.superlawva.global.security.util.JwtTokenProvider;
import com.superlawva.global.security.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final HashUtil hashUtil;

    @Override
    @Transactional
    public void register(UserRequestDTO userRequestDTO) {
        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new BaseException(ErrorStatus._EMAIL_ALREADY_EXISTS);
        }
        String hashedPassword = passwordEncoder.encode(userRequestDTO.getPassword());
        String emailHash = hashUtil.hash(userRequestDTO.getEmail());
        
        User user = User.builder()
                .email(userRequestDTO.getEmail())
                .emailHash(emailHash)
                .password(hashedPassword)
                .name(userRequestDTO.getName())
                .provider("LOCAL")
                .role(User.Role.USER)
                .build();
        userRepository.save(user);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        String emailHash = hashUtil.hash(loginRequestDTO.getEmail());
        User user = userRepository.findByEmailHash(emailHash)
                .orElseThrow(() -> new BaseException(ErrorStatus.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new BaseException(ErrorStatus._PASSWORD_NOT_MATCH);
        }

        String token = jwtTokenProvider.createToken(user.getEmail(), user.getId());
        
        // 프론트엔드 호환을 위한 목업 데이터 추가
        return LoginResponseDTO.builder()
                .token(token)
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getName())
                .provider(user.getProvider())
                .notification(List.of(0, 1, 2)) // 알림 목업 데이터
                .contractArray(List.of(
                    LoginResponseDTO.ContractInfo.builder()
                        ._id("contract_" + user.getId())
                        .title("월세 임대차 계약서")
                        .state("진행중")
                        .address("서울시 강남구 테헤란로 123")
                        .createdAt("2025.03.22")
                        .build()
                ))
                .recentChat(List.of(
                    LoginResponseDTO.RecentChat.builder()
                        ._id("1")
                        .title("집 주인이 보증금 안 돌려줘요.")
                        .build(),
                    LoginResponseDTO.RecentChat.builder()
                        ._id("2")
                        .title("전입 신고 방법 알려줘")
                        .build(),
                    LoginResponseDTO.RecentChat.builder()
                        ._id("3")
                        .title("묵시적 갱신이 뭔가요")
                        .build()
                ))
                .build();
    }

    @Override
    @Transactional
    public LoginResponseDTO kakaoLogin(KakaoLoginRequestDTO kakaoLoginRequestDTO) {
        String emailHash = hashUtil.hash(kakaoLoginRequestDTO.getEmail());
        User user = userRepository.findByEmailHash(emailHash)
                .orElseGet(() -> {
                    String newEmailHash = hashUtil.hash(kakaoLoginRequestDTO.getEmail());
                    return userRepository.save(User.builder()
                            .email(kakaoLoginRequestDTO.getEmail())
                            .emailHash(newEmailHash)
                            .name(kakaoLoginRequestDTO.getNickname())
                            .provider("KAKAO")
                            .role(User.Role.USER)
                            .emailVerified(true)
                            .build());
                });

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
        String emailHash = hashUtil.hash(naverLoginRequestDTO.getEmail());
        User user = userRepository.findByEmailHash(emailHash)
                .orElseGet(() -> {
                    String newEmailHash = hashUtil.hash(naverLoginRequestDTO.getEmail());
                    return userRepository.save(User.builder()
                            .email(naverLoginRequestDTO.getEmail())
                            .emailHash(newEmailHash)
                            .name(naverLoginRequestDTO.getName())
                            .provider("NAVER")
                            .role(User.Role.USER)
                            .emailVerified(true)
                            .build());
                });

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
        String emailHash = hashUtil.hash(email);
        return userRepository.findByEmailHash(emailHash)
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

    @Override
    public UserResponseDTO getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorStatus.MEMBER_NOT_FOUND));
        return UserResponseDTO.from(user);
    }

    @Override
    @Transactional
    public UserResponseDTO updateMyInfo(Long userId, UserRequestDTO.UpdateMyInfoDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorStatus.MEMBER_NOT_FOUND));
        
        if (request.getNickname() != null) {
            user.changeName(request.getNickname());
        }
        
        userRepository.save(user);
        return UserResponseDTO.from(user);
    }

    @Override
    @Transactional
    public void deleteMyAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorStatus.MEMBER_NOT_FOUND));
        userRepository.delete(user);
    }

    @Override
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponseDTO::from)
                .toList();
    }

    @Override
    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorStatus.MEMBER_NOT_FOUND));
        return UserResponseDTO.from(user);
    }

    @Override
    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorStatus.MEMBER_NOT_FOUND));
        
        if (dto.getName() != null) {
            user.changeName(dto.getName());
        }
        if (dto.getEmail() != null) {
            user.changeEmail(dto.getEmail());
        }
        
        userRepository.save(user);
        return UserResponseDTO.from(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(ErrorStatus.MEMBER_NOT_FOUND));
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public User processOAuth2User(String registrationId, Map<String, Object> attributes) {
        // OAuth2 사용자 처리 로직
        // 실제 구현에서는 registrationId에 따라 카카오/네이버 사용자 정보를 처리
        return null; // 임시 구현
    }

    @Override
    public LoginResponseDTO getUserDashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorStatus.MEMBER_NOT_FOUND));
        
        // 프론트엔드 호환을 위한 대시보드 데이터 반환
        return LoginResponseDTO.builder()
                .token("existing_token") // 기존 토큰 유지 (프론트에서 갱신하지 않을 경우)
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getName())
                .provider(user.getProvider())
                .notification(List.of(0, 1, 2)) // 알림 목업 데이터
                .contractArray(List.of(
                    LoginResponseDTO.ContractInfo.builder()
                        ._id("contract_" + user.getId())
                        .title("월세 임대차 계약서")
                        .state("진행중")
                        .address("서울시 강남구 테헤란로 123")
                        .createdAt("2025.03.22")
                        .build()
                ))
                .recentChat(List.of(
                    LoginResponseDTO.RecentChat.builder()
                        ._id("1")
                        .title("집 주인이 보증금 안 돌려줘요.")
                        .build(),
                    LoginResponseDTO.RecentChat.builder()
                        ._id("2")
                        .title("전입 신고 방법 알려줘")
                        .build(),
                    LoginResponseDTO.RecentChat.builder()
                        ._id("3")
                        .title("묵시적 갱신이 뭔가요")
                        .build()
                ))
                .build();
    }
}
