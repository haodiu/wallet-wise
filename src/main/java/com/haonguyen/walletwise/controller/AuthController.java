package com.haonguyen.walletwise.controller;

import com.haonguyen.walletwise.model.dto.JWTResponse;
import com.haonguyen.walletwise.model.dto.SignInRequest;
import com.haonguyen.walletwise.model.dto.SignUpRequest;
import com.haonguyen.walletwise.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<JWTResponse> signUp(
            @RequestBody SignUpRequest request
    ) {
        return authService.signUp(request);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<JWTResponse> signIn(
            @RequestBody SignInRequest request
    ) {
        return authService.signIn(request);
    }
}
