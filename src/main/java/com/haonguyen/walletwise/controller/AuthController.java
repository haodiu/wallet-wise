package com.haonguyen.walletwise.controller;

import com.haonguyen.walletwise.model.dto.JWTResponseDto;
import com.haonguyen.walletwise.model.dto.SignInRequestDto;
import com.haonguyen.walletwise.model.dto.SignUpRequestDto;
import com.haonguyen.walletwise.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserServiceImpl authService;

    @PostMapping("/sign-up")
    public ResponseEntity<JWTResponseDto> signUp(
            @Valid @RequestBody SignUpRequestDto request
    ) {
        return authService.signUp(request);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<JWTResponseDto> signIn(
            @Valid @RequestBody SignInRequestDto request
    ) {
        return authService.signIn(request);
    }
}
