package com.superlawva.domain.user.controller;

import com.superlawva.domain.user.dto.KakaoLoginRequestDTO;
import com.superlawva.domain.user.dto.LoginRequestDTO;
import com.superlawva.domain.user.dto.LoginResponseDTO;
import com.superlawva.domain.user.dto.NaverLoginRequestDTO;
import com.superlawva.domain.user.dto.SocialLoginCompleteDTO;
import com.superlawva.domain.user.dto.SocialLoginTempDTO;
import com.superlawva.domain.user.dto.UserRequestDTO;
import com.superlawva.domain.user.dto.UserResponseDTO;
import com.superlawva.domain.user.entity.User;
import com.superlawva.domain.user.repository.UserRepository;
import com.superlawva.domain.user.service.UserService;
import com.superlawva.global.response.ApiResponse;
import com.superlawva.global.security.util.HashUtil;
import com.superlawva.global.security.util.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "🔐 Authentication", description = "회원가입, 로그인, 소셜 로그인 관련 API")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final HashUtil hashUtil;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String naverRedirectUri;

    @PostMapping("/signup")
    @Operation(
        summary = "📝 일반 회원가입", 
        description = """
        이메일과 비밀번호를 사용하여 새로운 사용자를 등록합니다.
        
        **사용법:**
        1. 이메일, 비밀번호, 이름을 입력하여 요청
        2. 성공 시 회원가입 완료 메시지 반환
        3. 실패 시 적절한 에러 메시지 반환
        
        **주의사항:**
        - 이메일은 중복될 수 없습니다
        - 비밀번호는 암호화되어 저장됩니다
        - 비밀번호 복잡성 검증은 프론트엔드에서 처리됩니다
        - 회원가입 후 바로 로그인하려면 `/auth/login` API를 사용하세요
        """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "✅ 회원가입 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": true,
                        "code": "200",
                        "message": "요청에 성공했습니다.",
                        "result": "회원가입이 성공적으로 완료되었습니다."
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "❌ 잘못된 요청 데이터 (유효성 검사 실패)",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": false,
                        "code": "400",
                        "message": "잘못된 요청입니다.",
                        "result": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409", 
            description = "❌ 이미 존재하는 이메일",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": false,
                        "code": "4009",
                        "message": "이미 사용 중인 이메일입니다.",
                        "result": null
                    }
                    """
                )
            )
        )
    })
    public ApiResponse<String> signUp(@RequestBody @Valid UserRequestDTO request) {
        log.info("🔥 >>> /auth/signup 컨트롤러 도달! email: {}", request.getEmail());
        try {
            userService.register(request);
            log.info("✅ 회원가입 성공: {}", request.getEmail());
            return ApiResponse.onSuccess("회원가입이 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            log.error("❌ 회원가입 실패: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/login")
    @Operation(
        summary = "🔑 일반 로그인", 
        description = """
        이메일과 비밀번호로 로그인하고 JWT 토큰을 발급받습니다.
        
        **사용법:**
        1. 이메일과 비밀번호를 입력하여 요청
        2. 성공 시 JWT 토큰과 사용자 정보 반환
        3. 실패 시 적절한 에러 메시지 반환
        
        **응답 데이터:**
        - `token`: JWT 액세스 토큰 (Authorization 헤더에 "Bearer " + token 형태로 사용)
        - `email`: 사용자 이메일
        - `nickname`: 사용자 닉네임
        - `provider`: 로그인 제공자 (LOCAL, KAKAO, NAVER)
        
        **토큰 사용법:**
        이후 API 호출 시 Authorization 헤더에 "Bearer {token}" 형태로 포함하여 요청
        """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "✅ 로그인 성공",
            content = @Content(
                schema = @Schema(implementation = LoginResponseDTO.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": true,
                        "code": "200",
                        "message": "요청에 성공했습니다.",
                        "result": {
                            "token": "eyJhbGciOiJIUzI1NiJ9...",
                            "email": "user@example.com",
                            "nickname": "사용자",
                            "provider": "LOCAL"
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", 
            description = "❌ 인증 실패 (이메일 또는 비밀번호 불일치)",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": false,
                        "code": "USER400",
                        "message": "비밀번호가 일치하지 않습니다.",
                        "result": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404", 
            description = "❌ 존재하지 않는 사용자",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": false,
                        "code": "MEMBER4001",
                        "message": "사용자가 없습니다.",
                        "result": null
                    }
                    """
                )
            )
        )
    })
    public ApiResponse<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        return ApiResponse.onSuccess(userService.login(request));
    }

    @GetMapping("/oauth2/kakao")
    @Operation(
        summary = "🔗 카카오 OAuth2 인증 URL 생성", 
        description = """
        카카오 OAuth2 인증을 위한 URL을 생성합니다.
        
        **사용법:**
        1. 이 API를 호출하여 카카오 인증 URL을 받습니다
        2. 받은 URL로 사용자를 리다이렉트합니다
        3. 사용자가 카카오 로그인을 완료하면 `/auth/oauth2/callback/kakao`로 콜백됩니다
        
        **프론트엔드 구현 예시:**
        ```javascript
        // 1. 인증 URL 요청
        const response = await fetch('/auth/oauth2/kakao');
        const { authUrl } = await response.json();
        
        // 2. 카카오 로그인 페이지로 리다이렉트
        window.location.href = authUrl;
        ```
        
        **콜백 처리:**
        카카오 로그인 완료 후 자동으로 콜백 URL로 리다이렉트되며, 
        콜백 URL에서 JWT 토큰을 받아 로그인 처리를 완료합니다.
        """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "✅ 인증 URL 생성 성공",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": true,
                        "code": "200",
                        "message": "요청에 성공했습니다.",
                        "result": {
                            "authUrl": "https://kauth.kakao.com/oauth/authorize?client_id=...&redirect_uri=...&response_type=code&scope=profile_nickname"
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", 
            description = "❌ 서버 설정 오류",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": false,
                        "code": "COMMON500",
                        "message": "서버 에러, 관리자에게 문의 바랍니다.",
                        "result": null
                    }
                    """
                )
            )
        )
    })
    public ApiResponse<Map<String, String>> getKakaoAuthUrl(@RequestParam(required = false) String state) {
        String authUrl = UriComponentsBuilder
                .fromHttpUrl("https://kauth.kakao.com/oauth/authorize")
                .queryParam("client_id", kakaoClientId)
                .queryParam("redirect_uri", kakaoRedirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "profile_nickname")
                .build()
                .toUriString();

        return ApiResponse.onSuccess(Map.of("authUrl", authUrl));
    }

    @GetMapping("/oauth2/naver")
    @Operation(
        summary = "🔗 네이버 OAuth2 인증 URL 생성", 
        description = """
        네이버 OAuth2 인증을 위한 URL을 생성합니다.
        
        **사용법:**
        1. 이 API를 호출하여 네이버 인증 URL을 받습니다
        2. 받은 URL로 사용자를 리다이렉트합니다
        3. 사용자가 네이버 로그인을 완료하면 `/auth/oauth2/callback/naver`로 콜백됩니다
        
        **프론트엔드 구현 예시:**
        ```javascript
        // 1. 인증 URL 요청
        const response = await fetch('/auth/oauth2/naver');
        const { authUrl } = await response.json();
        
        // 2. 네이버 로그인 페이지로 리다이렉트
        window.location.href = authUrl;
        ```
        
        **콜백 처리:**
        네이버 로그인 완료 후 자동으로 콜백 URL로 리다이렉트되며, 
        콜백 URL에서 JWT 토큰을 받아 로그인 처리를 완료합니다.
        """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "✅ 인증 URL 생성 성공",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": true,
                        "code": "200",
                        "message": "요청에 성공했습니다.",
                        "result": {
                            "authUrl": "https://nid.naver.com/oauth2.0/authorize?client_id=...&redirect_uri=...&response_type=code&state=random_state"
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", 
            description = "❌ 서버 설정 오류",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": false,
                        "code": "COMMON500",
                        "message": "서버 에러, 관리자에게 문의 바랍니다.",
                        "result": null
                    }
                    """
                )
            )
        )
    })
    public ApiResponse<Map<String, String>> getNaverAuthUrl(@RequestParam(required = false) String state) {
        String authUrl = UriComponentsBuilder
                .fromHttpUrl("https://nid.naver.com/oauth2.0/authorize")
                .queryParam("client_id", naverClientId)
                .queryParam("redirect_uri", naverRedirectUri)
                .queryParam("response_type", "code")
                .queryParam("state", state != null ? state : "random_state")
                .build()
                .toUriString();

        return ApiResponse.onSuccess(Map.of("authUrl", authUrl));
    }

    @GetMapping("/oauth2/callback/kakao")
    @Operation(
        summary = "🔄 카카오 OAuth2 콜백 처리", 
        description = """
        카카오 OAuth2 인증 후 콜백을 처리합니다.
        
        **⚠️ 중요한 변경사항:**
        카카오에서 이메일 권한을 요청하지 않습니다.
        대신 프론트엔드에서 이메일을 직접 입력받는 2단계 프로세스로 구현되었습니다.
        
        **동작 과정:**
        1. 카카오에서 인가 코드(code)를 받습니다
        2. 인가 코드로 카카오 액세스 토큰을 요청합니다
        3. 액세스 토큰으로 카카오 사용자 정보(닉네임)를 조회합니다
        4. 임시 토큰을 발급하고 이메일 입력 요청
        
        **프론트엔드 처리 예시:**
        ```javascript
        // 콜백 응답 확인
        if (response.result.needEmail) {
            // 이메일 입력 화면으로 이동
            showEmailInputForm(response.result.tempToken);
        } else {
            // 바로 로그인 완료
            localStorage.setItem('token', response.result.token);
            navigateToMain();
        }
        ```
        
        **주의사항:**
        - 이 API는 카카오에서 자동으로 호출됩니다
        - 프론트엔드에서 직접 호출하지 마세요
        - 콜백 URL은 카카오 개발자 콘솔에 등록된 URL과 일치해야 합니다
        """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "⏳ 이메일 입력 필요 (임시 토큰 발급)",
            content = @Content(
                schema = @Schema(implementation = SocialLoginTempDTO.class),
                examples = @ExampleObject(
                    name = "카카오 로그인 성공",
                    value = """
                    {
                        "isSuccess": true,
                        "code": "200",
                        "message": "요청에 성공했습니다.",
                        "result": {
                            "tempToken": "temp_token_abc123",
                            "nickname": "카카오사용자",
                            "provider": "KAKAO",
                            "needEmail": true,
                            "message": "카카오 로그인이 완료되었습니다. 이메일을 입력해주세요."
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "❌ 유효하지 않은 인가 코드",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": false,
                        "code": "COMMON400",
                        "message": "잘못된 요청입니다.",
                        "result": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", 
            description = "❌ 카카오 서버 통신 오류",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": false,
                        "code": "KAKAO5001",
                        "message": "카카오 토큰 요청에 실패했습니다.",
                        "result": null
                    }
                    """
                )
            )
        )
    })
    public ApiResponse<Object> kakaoCallback(@RequestParam String code) {
        // 1. 인가 코드로 액세스 토큰 요청
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        String tokenBody = String.format("grant_type=authorization_code&client_id=%s&client_secret=%s&redirect_uri=%s&code=%s",
                kakaoClientId, kakaoClientSecret, kakaoRedirectUri, code);
        
        HttpEntity<String> tokenRequest = new HttpEntity<>(tokenBody, tokenHeaders);
        ResponseEntity<Map> tokenResponse = restTemplate.exchange(tokenUrl, HttpMethod.POST, tokenRequest, Map.class);
        
        String accessToken = (String) tokenResponse.getBody().get("access_token");
        
        // 2. 액세스 토큰으로 사용자 정보 요청
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);
        
        HttpEntity<String> userRequest = new HttpEntity<>(userHeaders);
        ResponseEntity<Map> userResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, userRequest, Map.class);
        
        Map<String, Object> userInfo = userResponse.getBody();
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        
        String nickname = (String) profile.get("nickname");
        Long kakaoId = ((Number) userInfo.get("id")).longValue();
        
        // 3. 이메일 권한을 요청하지 않으므로 항상 임시 토큰 발급
        String tempToken = jwtTokenProvider.createTempToken(kakaoId.toString(), "KAKAO", nickname);
        
        SocialLoginTempDTO tempResponse = SocialLoginTempDTO.builder()
                .tempToken(tempToken)
                .nickname(nickname)
                .provider("KAKAO")
                .needEmail(true)
                .message("카카오 로그인이 완료되었습니다. 이메일을 입력해주세요.")
                .build();
        
        return ApiResponse.onSuccess(tempResponse);
    }

    @GetMapping("/oauth2/callback/naver")
    @Operation(
        summary = "🔄 네이버 OAuth2 콜백 처리", 
        description = """
        네이버 OAuth2 인증 후 콜백을 처리하고 JWT 토큰을 발급합니다.
        
        **동작 과정:**
        1. 네이버에서 인가 코드(code)와 state를 받습니다
        2. 인가 코드로 네이버 액세스 토큰을 요청합니다
        3. 액세스 토큰으로 네이버 사용자 정보를 조회합니다
        4. 사용자 정보로 JWT 토큰을 생성하여 반환합니다
        
        **주의사항:**
        - 이 API는 네이버에서 자동으로 호출됩니다
        - 프론트엔드에서 직접 호출하지 마세요
        - 콜백 URL은 네이버 개발자 콘솔에 등록된 URL과 일치해야 합니다
        
        **응답 데이터:**
        일반 로그인과 동일한 LoginResponseDTO 형태로 JWT 토큰과 사용자 정보를 반환합니다.
        """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "✅ 네이버 로그인 성공",
            content = @Content(
                schema = @Schema(implementation = LoginResponseDTO.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": true,
                        "code": "200",
                        "message": "요청에 성공했습니다.",
                        "result": {
                            "token": "eyJhbGciOiJIUzI1NiJ9...",
                            "email": "user@naver.com",
                            "nickname": "네이버사용자",
                            "provider": "NAVER"
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "❌ 유효하지 않은 인가 코드 또는 state",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": false,
                        "code": "COMMON400",
                        "message": "잘못된 요청입니다.",
                        "result": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", 
            description = "❌ 네이버 서버 통신 오류",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": false,
                        "code": "NAVER5001",
                        "message": "네이버 토큰 요청에 실패했습니다.",
                        "result": null
                    }
                    """
                )
            )
        )
    })
    public ApiResponse<LoginResponseDTO> naverCallback(@RequestParam String code, @RequestParam String state) {
        // 1. 인가 코드로 액세스 토큰 요청
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        String tokenBody = String.format("grant_type=authorization_code&client_id=%s&client_secret=%s&redirect_uri=%s&code=%s&state=%s",
                naverClientId, naverClientSecret, naverRedirectUri, code, state);
        
        HttpEntity<String> tokenRequest = new HttpEntity<>(tokenBody, tokenHeaders);
        ResponseEntity<Map> tokenResponse = restTemplate.exchange(tokenUrl, HttpMethod.POST, tokenRequest, Map.class);
        
        String accessToken = (String) tokenResponse.getBody().get("access_token");
        
        // 2. 액세스 토큰으로 사용자 정보 요청
        String userInfoUrl = "https://openapi.naver.com/v1/nid/me";
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);
        
        HttpEntity<String> userRequest = new HttpEntity<>(userHeaders);
        ResponseEntity<Map> userResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, userRequest, Map.class);
        
        Map<String, Object> userInfo = userResponse.getBody();
        Map<String, Object> response = (Map<String, Object>) userInfo.get("response");
        
        String email = (String) response.get("email");
        String name = (String) response.get("name");
        
        // 3. 사용자 정보로 JWT 토큰 생성
        String emailHash = hashUtil.hash(email);
        User user = userRepository.findByEmailHash(emailHash)
                .orElseGet(() -> {
                    String newEmailHash = hashUtil.hash(email);
                    return userRepository.save(User.builder()
                            .email(email)
                            .emailHash(newEmailHash)
                            .name(name)
                            .provider("NAVER")
                            .role(User.Role.USER)
                            .emailVerified(true)
                            .build());
                });
        
        String jwtToken = jwtTokenProvider.createToken(user.getEmail(), user.getId());
        
        LoginResponseDTO loginResponse = LoginResponseDTO.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .nickname(user.getName())
                .provider(user.getProvider())
                .build();
        
        return ApiResponse.onSuccess(loginResponse);
    }

    @PostMapping("/login/kakao")
    @Operation(
        summary = "📱 카카오 소셜 로그인 (인가 코드 방식)", 
        description = """
        카카오 인가 코드를 받아 로그인 처리 후 JWT 토큰을 발급합니다.
        
        **사용법:**
        1. 프론트엔드에서 카카오 SDK를 사용하여 인가 코드를 받습니다
        2. 받은 인가 코드를 이 API로 전송합니다
        3. 성공 시 JWT 토큰과 사용자 정보를 반환합니다
        
        **프론트엔드 구현 예시:**
        ```javascript
        // 카카오 SDK 초기화 후
        Kakao.Auth.login({
            success: function(authObj) {
                // 인가 코드를 서버로 전송
                fetch('/auth/login/kakao', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        authorizationCode: authObj.code
                    })
                })
                .then(response => response.json())
                .then(data => {
                    // JWT 토큰 저장 및 로그인 처리
                    localStorage.setItem('token', data.result.token);
                });
            }
        });
        ```
        
        **주의사항:**
        - 이 방식은 카카오 SDK를 사용하는 경우에 적합합니다
        - OAuth2 URL 방식(`/auth/oauth2/kakao`)과는 다른 접근 방법입니다
        """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "✅ 로그인 성공",
            content = @Content(
                schema = @Schema(implementation = LoginResponseDTO.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": true,
                        "code": "200",
                        "message": "요청에 성공했습니다.",
                        "result": {
                            "token": "eyJhbGciOiJIUzI1NiJ9...",
                            "email": "user@kakao.com",
                            "nickname": "카카오사용자",
                            "provider": "KAKAO"
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "❌ 유효하지 않은 인가 코드",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": false,
                        "code": "COMMON400",
                        "message": "잘못된 요청입니다.",
                        "result": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", 
            description = "❌ 카카오 서버 통신 오류",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": false,
                        "code": "KAKAO5001",
                        "message": "카카오 토큰 요청에 실패했습니다.",
                        "result": null
                    }
                    """
                )
            )
        )
    })
    public ApiResponse<LoginResponseDTO> kakaoLogin(@RequestBody @Valid KakaoLoginRequestDTO request) {
        return ApiResponse.onSuccess(userService.kakaoLogin(request));
    }

    @PostMapping("/login/naver")
    @Operation(
        summary = "📱 네이버 소셜 로그인 (인가 코드 방식)", 
        description = """
        네이버 인가 코드를 받아 로그인 처리 후 JWT 토큰을 발급합니다.
        
        **사용법:**
        1. 프론트엔드에서 네이버 SDK를 사용하여 인가 코드를 받습니다
        2. 받은 인가 코드와 state를 이 API로 전송합니다
        3. 성공 시 JWT 토큰과 사용자 정보를 반환합니다
        
        **프론트엔드 구현 예시:**
        ```javascript
        // 네이버 SDK 초기화 후
        naverLogin.getLoginStatus(function(status) {
            if (status) {
                // 인가 코드를 서버로 전송
                fetch('/auth/login/naver', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        authorizationCode: naverLogin.authorizationCode,
                        state: naverLogin.state
                    })
                })
                .then(response => response.json())
                .then(data => {
                    // JWT 토큰 저장 및 로그인 처리
                    localStorage.setItem('token', data.result.token);
                });
            }
        });
        ```
        
        **주의사항:**
        - 이 방식은 네이버 SDK를 사용하는 경우에 적합합니다
        - OAuth2 URL 방식(`/auth/oauth2/naver`)과는 다른 접근 방법입니다
        """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "✅ 로그인 성공",
            content = @Content(
                schema = @Schema(implementation = LoginResponseDTO.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": true,
                        "code": "200",
                        "message": "요청에 성공했습니다.",
                        "result": {
                            "token": "eyJhbGciOiJIUzI1NiJ9...",
                            "email": "user@naver.com",
                            "nickname": "네이버사용자",
                            "provider": "NAVER"
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "❌ 유효하지 않은 인가 코드 또는 state",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": false,
                        "code": "COMMON400",
                        "message": "잘못된 요청입니다.",
                        "result": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", 
            description = "❌ 네이버 서버 통신 오류",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": false,
                        "code": "NAVER5001",
                        "message": "네이버 토큰 요청에 실패했습니다.",
                        "result": null
                    }
                    """
                )
            )
        )
    })
    public ApiResponse<LoginResponseDTO> naverLogin(@RequestBody @Valid NaverLoginRequestDTO request) {
        return ApiResponse.onSuccess(userService.naverLogin(request));
    }

    @PostMapping("/social/complete")
    @Operation(
        summary = "✅ 소셜 로그인 완료 (이메일 입력)", 
        description = """
        소셜 로그인에서 이메일 정보가 제공되지 않은 경우, 사용자가 이메일을 입력하여 로그인을 완료합니다.
        
        **사용 시나리오:**
        1. 카카오 로그인 시 이메일 정보가 제공되지 않음
        2. 서버에서 임시 토큰(`tempToken`)과 함께 `needEmail: true` 응답
        3. 사용자가 이메일을 입력
        4. 이 API로 임시 토큰과 이메일을 전송
        5. 최종 JWT 토큰 발급으로 로그인 완료
        
        **프론트엔드 구현 예시:**
        ```javascript
        // 카카오 콜백에서 needEmail이 true인 경우
        const completeLogin = async (tempToken, userEmail) => {
            const response = await fetch('/auth/social/complete', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    tempToken: tempToken,
                    email: userEmail
                })
            });
            
            const result = await response.json();
            if (result.isSuccess) {
                // JWT 토큰 저장 및 로그인 완료
                localStorage.setItem('token', result.result.token);
                navigateToMain();
            }
        };
        ```
        
        **주의사항:**
        - 임시 토큰은 30분간만 유효합니다
        - 이메일은 새로운 계정으로 등록되거나 기존 계정과 연결됩니다
        - 임시 토큰은 한 번만 사용 가능합니다
        """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", 
            description = "✅ 소셜 로그인 완료 성공",
            content = @Content(
                schema = @Schema(implementation = LoginResponseDTO.class),
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": true,
                        "code": "200",
                        "message": "요청에 성공했습니다.",
                        "result": {
                            "token": "eyJhbGciOiJIUzI1NiJ9...",
                            "email": "user@example.com",
                            "nickname": "카카오사용자",
                            "provider": "KAKAO"
                        }
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", 
            description = "❌ 유효하지 않은 임시 토큰 또는 이메일",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": false,
                        "code": "COMMON400",
                        "message": "임시 토큰이 유효하지 않거나 만료되었습니다.",
                        "result": null
                    }
                    """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409", 
            description = "❌ 이미 다른 소셜 계정으로 등록된 이메일",
            content = @Content(
                examples = @ExampleObject(
                    value = """
                    {
                        "isSuccess": false,
                        "code": "4009",
                        "message": "이미 사용 중인 이메일입니다.",
                        "result": null
                    }
                    """
                )
            )
        )
    })
    public ApiResponse<LoginResponseDTO> completeSocialLogin(@RequestBody @Valid SocialLoginCompleteDTO request) {
        try {
            // 1. 임시 토큰에서 소셜 정보 추출
            Claims tempClaims = jwtTokenProvider.getTempTokenClaims(request.getTempToken());
            String socialId = tempClaims.get("socialId", String.class);
            String provider = tempClaims.get("provider", String.class);
            String nickname = tempClaims.get("nickname", String.class);
            
            // 2. 사용자 등록 또는 조회
            String emailHash = hashUtil.hash(request.getEmail());
            User user = userRepository.findByEmailHash(emailHash)
                    .orElseGet(() -> {
                        String newEmailHash = hashUtil.hash(request.getEmail());
                        User.UserBuilder userBuilder = User.builder()
                                .email(request.getEmail())
                                .emailHash(newEmailHash)
                                .name(nickname)
                                .provider(provider)
                                .role(User.Role.USER)
                                .emailVerified(true);
                        
                        // 소셜 ID 설정
                        if ("KAKAO".equals(provider)) {
                            userBuilder.kakaoId(Long.parseLong(socialId));
                        } else if ("NAVER".equals(provider)) {
                            userBuilder.naverId(socialId);
                        }
                        
                        return userRepository.save(userBuilder.build());
                    });
            
            // 3. JWT 토큰 생성
            String jwtToken = jwtTokenProvider.createToken(user.getEmail(), user.getId());
            
            LoginResponseDTO loginResponse = LoginResponseDTO.builder()
                    .token(jwtToken)
                    .email(user.getEmail())
                    .nickname(user.getName())
                    .provider(user.getProvider())
                    .build();
            
            return ApiResponse.onSuccess(loginResponse);
            
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("임시 토큰이 유효하지 않거나 만료되었습니다.", e);
        } catch (Exception e) {
            throw new RuntimeException("소셜 로그인 완료 처리 중 오류가 발생했습니다.", e);
        }
    }
} 