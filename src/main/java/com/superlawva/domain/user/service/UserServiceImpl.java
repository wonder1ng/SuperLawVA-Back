package com.superlawva.domain.user.service;

import com.superlawva.domain.user.dto.LoginRequestDTO;
import com.superlawva.domain.user.dto.LoginResponseDTO;
import com.superlawva.domain.user.dto.PasswordChangeRequestDTO;
import com.superlawva.domain.user.dto.UserRequestDTO;
import com.superlawva.domain.user.dto.UserResponseDTO;
import com.superlawva.domain.user.entity.User;
import com.superlawva.domain.user.repository.UserRepository;
import com.superlawva.global.exception.BaseException;
import com.superlawva.global.response.status.ErrorStatus;
import com.superlawva.global.security.util.HashUtil;
import com.superlawva.global.security.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HashUtil hashUtil;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponseDTO::of)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO findById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("회원이 없습니다."));
        return UserResponseDTO.of(u);
    }

    @Override
    @Transactional
    public UserResponseDTO create(UserRequestDTO dto) {
        String emailHash = hashUtil.hash(dto.getEmail());
        if (userRepository.existsByEmailHash(emailHash)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        
        // 일반 회원가입 시 password 필수 검증
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        
        User user = User.builder()
                .email(dto.getEmail())
                .emailHash(emailHash)
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .build();
        User saved = userRepository.save(user);
        return UserResponseDTO.of(saved);
    }

    @Override
    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("회원이 없습니다."));
        
        // 이메일 변경 시 해시도 함께 업데이트
        if (dto.getEmail() != null && !dto.getEmail().equals(u.getEmail())) {
            u.setEmail(dto.getEmail());
            u.setEmailHash(hashUtil.hash(dto.getEmail()));
        }

        u.changeNickname(dto.getNickname());
        User saved = userRepository.save(u);
        return UserResponseDTO.of(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserResponseDTO getMyInfo(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new BaseException(ErrorStatus._USER_NOT_FOUND));
        return UserResponseDTO.of(user);
    }

    @Override
    @Transactional
    public UserResponseDTO updateMyInfo(Long userId, UserRequestDTO.UpdateMyInfoDTO dto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorStatus._USER_NOT_FOUND));
        user.changeNickname(dto.getNickname());
        return UserResponseDTO.of(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteMyAccount(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorStatus._USER_NOT_FOUND));
        user.softDelete(); // deletedAt 필드를 현재 시간으로 설정
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(Long id, PasswordChangeRequestDTO request) {
        User user = userRepository.findById(id).orElseThrow(() -> new BaseException(ErrorStatus._USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BaseException(ErrorStatus._PASSWORD_NOT_MATCH);
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BaseException(ErrorStatus._PASSWORD_CONFIRM_NOT_MATCH);
        }

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserResponseDTO signUp(UserRequestDTO.SignUpDTO request) {
        String emailHash = hashUtil.hash(request.getEmail());
        if (userRepository.findByEmailHash(emailHash).isPresent()) {
            throw new BaseException(ErrorStatus._EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .email(request.getEmail())
                .emailHash(emailHash)
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();

        User savedUser = userRepository.save(user);
        return UserResponseDTO.of(savedUser);
    }

    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        String emailHash = hashUtil.hash(request.getEmail());
        User user = userRepository.findByEmailHash(emailHash)
                .orElseThrow(() -> new BaseException(ErrorStatus._USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BaseException(ErrorStatus._PASSWORD_NOT_MATCH);
        }

        String accessToken = jwtTokenProvider.createToken(user.getEmail());

        return LoginResponseDTO.builder()
                .token(accessToken)
                .userName(user.getNickname())
                .notification(getNotificationCounts(user.getId()))
                .contractArray(getRecentContract(user.getId()))
                .recentChat(getRecentChats(user.getId()))
                .build();
    }

    // 알림 개수 조회 메서드 (하드코딩)
    private List<Integer> getNotificationCounts(Long userId) {
        try {
            // TODO: 나중에 실제 알림 테이블 구현되면 아래와 같이 변경
            // int unread = alarmRepository.countByUserIdAndIsReadFalse(userId);
            // int urgent = alarmRepository.countUrgentByUserId(userId);
            // int total = alarmRepository.countByUserId(userId);
            // return Arrays.asList(unread, urgent, total);
            return List.of(0, 1, 2); // [읽지않은, 긴급, 전체]
        } catch (Exception e) {
            return List.of(0, 0, 0);
        }
    }

    // 계약 정보 조회 메서드 (하드코딩)
    private List<LoginResponseDTO.ContractInfo> getRecentContract(Long userId) {
        try {
            LoginResponseDTO.ContractInfo contractInfo = LoginResponseDTO.ContractInfo.builder()
                    ._id("asdasd")
                    .title("월세 임대차 계약서")
                    .state("진행중")
                    .address("서울시 강남구 테헤란로 123")
                    .createdAt("2025.03.22")
                    .build();
            return List.of(contractInfo);
        } catch (Exception e) {
            return List.of();
        }
    }

    // 최근 채팅 목록 조회 메서드 (하드코딩)
    private List<LoginResponseDTO.RecentChat> getRecentChats(Long userId) {
        try {
            return List.of(
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
            );
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    @Transactional
    public void processOAuth2User(String registrationId, Map<String, Object> attributes) {
        String email = null;
        String nickname = null;
        String naverId = null;
        Long kakaoId = null;

        if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            email = (String) response.get("email");
            nickname = (String) response.get("nickname");
            naverId = (String) response.get("id");
        } else if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            email = (String) kakaoAccount.get("email");
            nickname = (String) profile.get("nickname");
            kakaoId = (Long) attributes.get("id");
        }

        if (email == null) {
            // 이메일 동의를 하지 않은 경우 처리
            throw new IllegalArgumentException("소셜 로그인 시 이메일 제공 동의가 필요합니다.");
        }
        
        String emailHash = hashUtil.hash(email);
        Optional<User> existing = userRepository.findByEmailHash(emailHash);

        if (existing.isEmpty()) {
            User u = User.builder()
                    .email(email)
                    .emailHash(emailHash)
                    .nickname(nickname)
                    .kakaoId(kakaoId)
                    .naverId(naverId)
                    .emailVerified(true) // 소셜 로그인은 이메일 인증된 것으로 간주
                    .build();
            userRepository.save(u);
        } else {
            // 기존 회원이 다른 소셜로 연동 시도하는 경우 등 확장 가능
            User user = existing.get();
            if (nickname != null && !nickname.equals(user.getNickname())) {
                user.changeNickname(nickname);
                userRepository.save(user);
            }
        }
    }
}
