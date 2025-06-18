package com.superlawva.domain.user.controller;

import com.superlawva.global.verification.dto.request.EmailSendRequestDTO;
import com.superlawva.global.verification.dto.request.EmailVerifyRequestDTO;
import com.superlawva.global.verification.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService svc;

    @PostMapping("/send")
    public ResponseEntity<Void> send(@RequestBody EmailSendRequestDTO req) {
        svc.sendVerification(req.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verify(@RequestBody EmailVerifyRequestDTO req) {
        boolean ok = svc.verifyToken(req.getEmail(), req.getCode());
        if (!ok) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
}
