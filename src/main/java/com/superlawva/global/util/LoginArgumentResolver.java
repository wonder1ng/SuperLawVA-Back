package com.superlawva.global.util;

import com.superlawva.domain.user.entity.User;
import com.superlawva.domain.user.repository.UserRepository;
import com.superlawva.global.security.annotation.LoginUser;
import com.superlawva.global.security.util.HashUtil;
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
    private final HashUtil hashUtil;

    @Override
    public boolean supportsParameter(MethodParameter param) {
        return param.hasParameterAnnotation(LoginUser.class) &&
                (param.getParameterType().equals(User.class) || param.getParameterType().equals(Long.class));
    }

    @Override
    public Object resolveArgument(MethodParameter param,
                                  ModelAndViewContainer mav,
                                  NativeWebRequest webRequest,
                                  org.springframework.web.bind.support.WebDataBinderFactory binder) {

        // ✅ SecurityContextHolder에서 email을 가져옴 (JwtAuthFilter에서 Principal로 email 설정했을 경우)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        String email = (String) auth.getPrincipal();
        String emailHash = hashUtil.hash(email);

        // ✅ @LoginUser User
        if (param.getParameterType().equals(User.class)) {
            return userRepository.findByEmailHash(emailHash).orElse(null);
        }

        // ✅ @LoginUser Long
        if (param.getParameterType().equals(Long.class)) {
            User user = userRepository.findByEmailHash(emailHash).orElse(null);
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
