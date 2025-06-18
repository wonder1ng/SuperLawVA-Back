package com.superlawva.global.security.controller;

import com.superlawva.global.security.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenController {

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/token/{kakaoId}")
    public String token(@PathVariable Long kakaoId) {
        return jwtTokenProvider.createToken(kakaoId.toString()); // subject는 String
    }
}
