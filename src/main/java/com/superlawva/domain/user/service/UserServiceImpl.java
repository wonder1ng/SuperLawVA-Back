package com.superlawva.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.superlawva.domain.user.dto.*;
import com.superlawva.domain.user.entity.User;
import com.superlawva.domain.user.repository.UserRepository;
import com.superlawva.global.exception.BaseException;
import com.superlawva.global.response.status.ErrorStatus;
import com.superlawva.global.security.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;


    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String naverTokenUri;

    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String naverUserInfoUri;

    @Override
    public List<UserResponseDTO> findAll() { return null; }

    @Override
    public UserResponseDTO findById(Long id) { return null; }

    @Override
    public UserResponseDTO create(UserRequestDTO dto) { return null; }

    @Override
    public UserResponseDTO update(Long id, UserRequestDTO dto) { return null; }

    @Override
    public void delete(Long id) { }

    @Override
    public UserResponseDTO getMyInfo(Long userId) { return null; }

    @Override
    public UserResponseDTO updateMyInfo(Long userId, UserRequestDTO.UpdateMyInfoDTO dto) { return null; }

    @Override
    public void deleteMyAccount(Long userId) { }

    @Override
    public void processOAuth2User(String registrationId, Map<String,Object> attributes) { }


    @Override
    @Transactional
    public UserResponseDTO signUp(UserRequestDTO.SignUpDTO request) {
        checkEmailDuplication(request.getEmail());
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BaseException(ErrorStatus._PASSWORD_CONFIRM_NOT_MATCH);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .provider("LOCAL")
                .build();
        userRepository.save(user);
        return UserResponseDTO.from(user);
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorStatus._USER_NOT_FOUND));
    }

    @Override
    public void checkEmailDuplication(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new BaseException(ErrorStatus._EMAIL_ALREADY_EXISTS);
        });
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(ErrorStatus._USER_NOT_FOUND));
    }

    @Override
    public List<UserResponseDTO> getUsers(String keyword) {
        List<User> users = userRepository.findByNameContaining(keyword);
        return users.stream().map(UserResponseDTO::from).collect(Collectors.toList());
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = findByEmail(request.getEmail());
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BaseException(ErrorStatus._PASSWORD_NOT_MATCH);
        }
        String token = jwtTokenProvider.createToken(user.getEmail(), user.getId());
        return LoginResponseDTO.builder()
                .token(token)
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .provider(user.getProvider())
                .build();
    }

    @Override
    @Transactional
    public LoginResponseDTO kakaoLogin(KakaoLoginRequestDTO request) {
        String accessToken = getKakaoAccessToken(request.getAuthorizationCode());
        Map<String, Object> userInfo = getKakaoUserInfo(accessToken);

        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String email = (String) kakaoAccount.get("email");
        String name = (String) profile.get("nickname");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .name(name)
                            .provider("KAKAO")
                            .build();
                    return userRepository.save(newUser);
                });

        String jwtToken = jwtTokenProvider.createToken(user.getEmail(), user.getId());
        return LoginResponseDTO.builder()
                .token(jwtToken)
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .provider(user.getProvider())
                .build();
    }

    @Override
    @Transactional
    public LoginResponseDTO naverLogin(NaverLoginRequestDTO request) {
        String accessToken = getNaverAccessToken(request.getAuthorizationCode(), request.getState());
        Map<String, Object> userInfo = getNaverUserInfo(accessToken);

        Map<String, Object> responseAttributes = (Map<String, Object>) userInfo.get("response");
        String email = (String) responseAttributes.get("email");
        String name = (String) responseAttributes.get("name");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .name(name)
                            .provider("NAVER")
                            .build();
                    return userRepository.save(newUser);
                });

        String jwtToken = jwtTokenProvider.createToken(user.getEmail(), user.getId());
        return LoginResponseDTO.builder()
                .token(jwtToken)
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .provider(user.getProvider())
                .build();
    }

    @Override
    public UserResponseDTO.MyPageDTO getMyPage(User user) {
        return null;
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
    }

    private String getKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(kakaoTokenUri, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
                return (String) responseBody.get("access_token");
            }
        } catch (JsonProcessingException e) {
            throw new BaseException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
        throw new BaseException(ErrorStatus.KAKAO_TOKEN_REQUEST_FAILED);
    }

    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(kakaoUserInfoUri, HttpMethod.GET, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return objectMapper.readValue(response.getBody(), Map.class);
            }
        } catch (JsonProcessingException e) {
            throw new BaseException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
        throw new BaseException(ErrorStatus.KAKAO_USER_INFO_FAILED);
    }

    private String getNaverAccessToken(String authorizationCode, String state) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverClientId);
        params.add("client_secret", naverClientSecret);
        params.add("code", authorizationCode);
        params.add("state", state);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(naverTokenUri, requestEntity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return (String) response.getBody().get("access_token");
            }
        } catch (Exception e) {
            throw new BaseException(ErrorStatus.NAVER_TOKEN_REQUEST_FAILED);
        }
        throw new BaseException(ErrorStatus.NAVER_TOKEN_REQUEST_FAILED);
    }

    private Map<String, Object> getNaverUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(naverUserInfoUri, HttpMethod.GET, requestEntity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
        } catch (Exception e) {
            throw new BaseException(ErrorStatus.NAVER_USER_INFO_FAILED);
        }
        throw new BaseException(ErrorStatus.NAVER_USER_INFO_FAILED);
    }
}
