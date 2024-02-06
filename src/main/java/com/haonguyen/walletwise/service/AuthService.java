package com.haonguyen.walletwise.service;

import com.haonguyen.walletwise.model.dto.JWTResponse;
import com.haonguyen.walletwise.model.dto.SignInRequest;
import com.haonguyen.walletwise.model.dto.SignUpRequest;
import com.haonguyen.walletwise.model.dto.TokenDto;
import com.haonguyen.walletwise.model.entity.User;
import com.haonguyen.walletwise.repository.IRoleRepository;
import com.haonguyen.walletwise.repository.IUserRepository;
import com.haonguyen.walletwise.security.jwt.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final JWTUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<JWTResponse> signUp(SignUpRequest request) {
        // Check if the user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent() ||
                userRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        // Convert SignUpRequest to User entity
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(roleRepository.findByName("user"))
                .build();
        // Save the user to database
        userRepository.save(user);
        return createJWTResponse(user);
    }

    public ResponseEntity<JWTResponse> signIn(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        return createJWTResponse(user);
    }

    /**
     * Creates a JWTResponse object for the given user.
     *
     * @param user The user object for which the JWTResponse is generated.
     * @return The JWTResponse object containing JWT tokens and user information.
     */
    private ResponseEntity<JWTResponse> createJWTResponse(User user) {
        String jwtToken = jwtUtils.generateToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
        TokenDto tokenDto = TokenDto.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
        return ResponseEntity.ok(JWTResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().getName())
                .token(tokenDto)
                .build());
    }
}