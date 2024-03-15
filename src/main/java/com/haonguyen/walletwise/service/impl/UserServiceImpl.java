package com.haonguyen.walletwise.service.impl;

import com.haonguyen.walletwise.common.RoleType;
import com.haonguyen.walletwise.exception.BadRequestException;
import com.haonguyen.walletwise.exception.NotFoundException;
import com.haonguyen.walletwise.exception.UnauthorizedException;
import com.haonguyen.walletwise.model.dto.*;
import com.haonguyen.walletwise.model.entity.Category;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IBaseService<UserDetailDto, Long>, IModelMapper<UserDetailDto, User> {
    private final IUserRepository iUserRepository;
    private final RoleServiceImpl roleService;
    private final EmailSenderServiceImpl emailSenderService;
    private final CategoryServiceImpl categoryService;
    private final TransactionServiceImpl transactionService;
    private final PasswordResetTokenServiceImpl passwordSenderService;
    private final JWTUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private void validateSignUpRequest(SignUpRequestDto request) {
        if (iUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("User with this email already exists");
        }
        if (iUserRepository.findByName(request.getName()).isPresent()) {
            throw new BadRequestException("User with this name already exists");
        }
    }

    private User createUserFromSignUpRequest(SignUpRequestDto request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Role userRole = roleService.findByName("user");

        return User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(encodedPassword)
                .role(userRole)
                .build();
    }
    public ResponseEntity<JWTResponseDto> signUp(SignUpRequestDto request) {
        validateSignUpRequest(request);
        User user = createUserFromSignUpRequest(request);
        iUserRepository.save(user);
        return createJWTResponse(user);
    }

    public ResponseEntity<JWTResponseDto> signIn(SignInRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new UnauthorizedException("Unauthorized: Invalid credentials");
        }
        User user = iUserRepository.findByEmail(request.getEmail()).orElseThrow();
        return createJWTResponse(user);
    }

    /**
     * Creates a JWTResponse object for the given user.
     *
     * @param user The user object for which the JWTResponse is generated.
     * @return The JWTResponse object containing JWT tokens and user information.
     */
    private ResponseEntity<JWTResponseDto> createJWTResponse(User user) {
        String jwtToken = jwtUtils.generateToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
        TokenDto tokenDto = TokenDto.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
        return ResponseEntity.ok(JWTResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().getName())
                .token(tokenDto)
                .build());
    }

    private User getDetail(Long id) {
        return iUserRepository.findById(id)
                .orElse(new User());
    }

    public User loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return iUserRepository.findByEmail(username).orElseThrow();
    }

    @Override
    public List<UserDetailDto> findAll(Integer pageNo, Integer pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        Page<User> pagedResult = iUserRepository.findAll(paging);

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
        return iUserRepository.findById(id)
                .map(this::createFromE)
                .orElseThrow(() -> new NotFoundException(User.class, id));
    }

    @Override
    public UserDetailDto update(Long id, UserDetailDto dto) {
        User entity = iUserRepository.findById(id).orElseThrow(() -> new NotFoundException(User.class, id));
        entity = updateEntity(entity, dto);
        return createFromE(iUserRepository.save(entity));
    }

    @Override
    public UserDetailDto save(UserDetailDto dto) {
        logger.error(dto.getEmail() + dto.getName() + dto.getRole() + dto.getPassword());
        logger.error(" this dto: ", dto);
        User user = createFromD(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        iUserRepository.save(user);
        return createFromE(user);
    }

    @Override
    public void delete(Long id) {
        User entity = iUserRepository.findById(id).orElseThrow(() -> new NotFoundException(User.class, id));
        if (entity.getRole().getName().equals(RoleType.ADMIN.getRoleName())) {
            throw new BadRequestException("Permission denied");
        }
        iUserRepository.deleteById(id);
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
    public User updateEntity(User entity, UserDetailDto dto) {
        // Skip null fields when mapping
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(dto, entity);
        return entity;
    }

    public ResponseEntity<MessageResponseDto> createPasswordResetTokenForUser(String email) {
        User user = iUserRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        String token = UUID.randomUUID().toString();
        passwordSenderService.save(token, user);
        //Send token to email
        emailSenderService.sendEmail(user.getEmail(), "Password Reset Token","Token: " + token);
        return ResponseEntity.ok(MessageResponseDto.builder()
                .message("Password reset token sent to your email")
                .build());
    }

    public ResponseEntity<MessageResponseDto> changePassword(ChangePasswordDto changePasswordDto) {
        String token = changePasswordDto.getToken();
        String email = changePasswordDto.getEmail();
        String newPassword = changePasswordDto.getNewPassword();
        User user = iUserRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        Long userId = user.getId();
        // check token exists and not expired
        Boolean isValid = passwordSenderService.isValidToken(userId, token);
        if (isValid) {
            String passwordEncoded = passwordEncoder.encode(newPassword);
            user.setPassword(passwordEncoded);
            iUserRepository.save(user);
            return ResponseEntity.ok(MessageResponseDto.builder()
                    .message("Successfully")
                    .build());
        } else {
            return ResponseEntity.ok(MessageResponseDto.builder()
                    .message("Token invalid")
                    .build());
        }
    }

    public ResponseEntity<?> getCategories(String categoryName, String sortBy) {
        if (categoryName == null) {
            List<CategoryDto> categoryDtos = categoryService.findAll();
            return ResponseEntity.ok(categoryDtos);
        } else {
            CategoryDto categoryDto = categoryService.findByName(categoryName, sortBy);
            return ResponseEntity.ok(categoryDto);
        }
    }

//    public ResponseEntity<List<TotalAmountDto>> computeTotalAmount(String start, String end) {
//        // Chuyển đổi chuỗi ngày thành LocalDate
//        LocalDate startDate = start != null ? LocalDate.parse(start) : null;
//        LocalDate endDate = end != null ? LocalDate.parse(end) : null;
//        List<TotalAmountDto> totalAmountDtoList = transactionService.computeTotalAmount(toDate(startDate), toDate(endDate));
//        return ResponseEntity.ok(totalAmountDtoList);
//    }

    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        LocalDateTime localDateTime = localDate.atStartOfDay();
        return Date.from(localDateTime.toInstant(java.time.ZoneOffset.UTC));
    }
}