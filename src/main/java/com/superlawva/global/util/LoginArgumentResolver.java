package com.superlawva.global.util;

import com.superlawva.domain.user.entity.User;
import com.superlawva.domain.user.repository.UserRepository;
import com.superlawva.global.security.annotation.LoginUser;
import com.superlawva.global.security.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter param) {
        return param.hasParameterAnnotation(LoginUser.class) && (
                param.getParameterType().equals(User.class) || 
                param.getParameterType().equals(Long.class)
        );
    }

    @Override
    public Object resolveArgument(MethodParameter param,
                                  ModelAndViewContainer mav,
                                  NativeWebRequest webRequest,
                                  org.springframework.web.bind.support.WebDataBinderFactory binder) {

        // SecurityContext에서 email 가져오기 (JWT 필터에서 설정됨)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        String email = (String) auth.getPrincipal();
        
        // User 타입 요청 시
        if (param.getParameterType().equals(User.class)) {
            return userRepository.findByEmail(email).orElse(null);
        }
        
        // Long 타입 요청 시 (기존 호환성 유지)
        if (param.getParameterType().equals(Long.class)) {
            User user = userRepository.findByEmail(email).orElse(null);
            return user != null ? user.getId() : null;
        }

        return null;
    }

    private String extractToken(HttpServletRequest req) {
        String bearer = req.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("ACCESS_TOKEN".equals(c.getName()) && StringUtils.hasText(c.getValue())) {
                    return c.getValue();
                }
            }
        }

        return null;
    }
}
