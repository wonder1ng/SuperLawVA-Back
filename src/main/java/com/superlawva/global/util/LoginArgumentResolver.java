package com.superlawva.global.util;

import com.superlawva.global.security.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
@Component
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter param) {
        return param.hasParameterAnnotation(LoginUser.class)
                && param.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter param,
                                  ModelAndViewContainer mav,
                                  NativeWebRequest webRequest,
                                  org.springframework.web.bind.support.WebDataBinderFactory binder) {

        HttpServletRequest req = (HttpServletRequest) webRequest.getNativeRequest();
        String token = extractToken(req);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String subject = jwtTokenProvider.getSubject(token);
            return Long.parseLong(subject); // kakaoId나 userId
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
