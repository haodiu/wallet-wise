package com.haonguyen.walletwise.service.impl;

import com.haonguyen.walletwise.exception.NotFoundException;
import com.haonguyen.walletwise.model.dto.*;
import com.haonguyen.walletwise.model.entity.Role;
import com.haonguyen.walletwise.model.entity.User;
import com.haonguyen.walletwise.repository.IRoleRepository;
import com.haonguyen.walletwise.repository.IUserRepository;
import com.haonguyen.walletwise.security.jwt.JWTUtils;
import com.haonguyen.walletwise.service.IBaseService;
import com.haonguyen.walletwise.service.IModelMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IBaseService<UserDetailDto, Long>, IModelMapper<UserDetailDto, User> {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final JWTUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private void validateSignUpRequest(SignUpRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        if (userRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("User with this name already exists");
        }
    }

    private User createUserFromSignUpRequest(SignUpRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Role userRole = roleRepository.findByName("user")
                .orElseThrow(() -> new IllegalStateException("Default role not found"));

        return User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(encodedPassword)
                .role(userRole)
                .build();
    }
    public ResponseEntity<JWTResponse> signUp(SignUpRequest request) {
        // Check if the user already exists
        validateSignUpRequest(request);
        // Create User entity from SignUpRequest
        User user = createUserFromSignUpRequest(request);
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

    private User getDetail(Long id) {
        return userRepository.findById(id)
                .orElse(new User());
    }

    public User loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow();
    }

    @Override
    public List<UserDetailDto> findAll() {
        // start add pagination and sorting

        // end
        return userRepository.findAll().stream()
                .map(this::createFromE)
                .collect(Collectors.toList());
    }

    @Override
    public UserDetailDto findById(Long id) {
        return userRepository.findById(id)
                .map(this::createFromE)
                .orElseThrow(() -> new NotFoundException(User.class, id));
    }

    @Override
    public UserDetailDto update(Long id, UserDetailDto dto) {
        User entity = userRepository.findById(id).orElseThrow(() -> new NotFoundException(User.class, id));
        updateEntity(entity, dto);
        return createFromE(userRepository.save(entity));
    }

    @Override
    public UserDetailDto save(UserDetailDto dto) {
        User user = createFromD(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        return createFromE(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User createFromD(UserDetailDto dto) {
        User entity = modelMapper.map(dto, User.class);
        String roleName = Optional.ofNullable(dto.getRole()).orElseThrow(() -> new IllegalArgumentException("Role cannot be null"));
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new NotFoundException("Not not found"));
        entity.setRole(role);
        return entity;
    }

    @Override
    public UserDetailDto createFromE(User entity) {
        UserDetailDto dto = modelMapper.map(entity, UserDetailDto.class);
        dto.setRole(entity.getRole().getName());
        return dto;
    }

    /**
     * Updates a User entity with the details from a DTO object
     *
     * @param entity - the User entity to update
     * @param dto    - the DTO object containing update details
     * @throws IllegalArgumentException - if the role in the dto is null
     * @throws NotFoundException        - if the role specified in the dto is not found in the repository
     **/
    @Override
    public void updateEntity(User entity, UserDetailDto dto) {
        if (entity != null && dto != null) {
            String roleName = Optional.ofNullable(dto.getRole())
                    .orElseThrow(() -> new IllegalArgumentException("Role cannot be null"));
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new NotFoundException("Role not found"));
            entity.setRole(role);
            entity.setName(dto.getName());
        }
    }
}