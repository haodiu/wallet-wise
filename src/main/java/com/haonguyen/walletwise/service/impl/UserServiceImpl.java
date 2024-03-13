package com.haonguyen.walletwise.service.impl;

import com.haonguyen.walletwise.common.RoleType;
import com.haonguyen.walletwise.exception.BadRequestException;
import com.haonguyen.walletwise.exception.NotFoundException;
import com.haonguyen.walletwise.exception.UnauthorizedException;
import com.haonguyen.walletwise.model.dto.*;
import com.haonguyen.walletwise.model.entity.Role;
import com.haonguyen.walletwise.model.entity.User;
import com.haonguyen.walletwise.repository.IUserRepository;
import com.haonguyen.walletwise.security.jwt.JWTUtils;
import com.haonguyen.walletwise.service.IBaseService;
import com.haonguyen.walletwise.service.IModelMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IBaseService<UserDetailDto, Long>, IModelMapper<UserDetailDto, User> {
    private final IUserRepository userRepository;
    private final RoleServiceImpl roleService;
    private final EmailSenderServiceImpl emailSenderService;
    private final PasswordResetTokenServiceImpl passwordSenderService;
    private final JWTUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private void validateSignUpRequest(SignUpRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("User with this email already exists");
        }
        if (userRepository.findByName(request.getName()).isPresent()) {
            throw new BadRequestException("User with this name already exists");
        }
    }

    private User createUserFromSignUpRequest(SignUpRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Role userRole = roleService.findByName("user");

        return User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(encodedPassword)
                .role(userRole)
                .build();
    }
    public ResponseEntity<JWTResponse> signUp(SignUpRequest request) {
        validateSignUpRequest(request);
        User user = createUserFromSignUpRequest(request);
        userRepository.save(user);
        return createJWTResponse(user);
    }

    public ResponseEntity<JWTResponse> signIn(SignInRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new UnauthorizedException("Unauthorized: Invalid credentials");
        }
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
    public List<UserDetailDto> findAll(Integer pageNo, Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<User> pagedResult = userRepository.findAll(paging);

        if (pagedResult.hasContent()) {
            return pagedResult.getContent().stream()
                    .map(this::createFromE)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
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
        logger.error(dto.getEmail() + dto.getName() + dto.getRole() + dto.getPassword());
        logger.error(" this dto: ", dto);
        User user = createFromD(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
        return createFromE(user);
    }

    @Override
    public void delete(Long id) {
        User entity = userRepository.findById(id).orElseThrow(() -> new NotFoundException(User.class, id));
        if (entity.getRole().getName().equals(RoleType.ADMIN.getRoleName())) {
            throw new BadRequestException("Permission denied");
        }
        userRepository.deleteById(id);
    }

    @Override
    public User createFromD(UserDetailDto dto) {
        User entity = modelMapper.map(dto, User.class);
        String roleName = Optional.ofNullable(dto.getRole()).orElseThrow(() -> new IllegalArgumentException("Role cannot be null"));
        Role role = roleService.findByName(roleName);
        entity.setRole(role);
        return entity;
    }

    @Override
    public UserDetailDto createFromE(User entity) {
        UserDetailDto dto = modelMapper.map(entity, UserDetailDto.class);
        dto.setRole(entity.getRole().getName());
        dto.setPassword(null);
        return dto;
    }

    @Override
    public void updateEntity(User entity, UserDetailDto dto) {
        if (entity == null || dto == null) {
            throw new BadRequestException("Entity and DTO cannot be null");
        }
        entity.setName(dto.getName());
    }

    public ResponseEntity<MessageResponse> createPasswordResetTokenForUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        String token = UUID.randomUUID().toString();
        passwordSenderService.save(token, user);
        //Send token to email
        emailSenderService.sendEmail(user.getEmail(), "Password Reset Token","Token: " + token);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Password reset token sent to your email")
                .build());
    }
}