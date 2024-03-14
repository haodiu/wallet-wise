package com.haonguyen.walletwise.controller.impl;

import com.haonguyen.walletwise.controller.IBaseController;
import com.haonguyen.walletwise.model.dto.ChangePasswordDto;
import com.haonguyen.walletwise.model.dto.UserDetailDto;
import com.haonguyen.walletwise.service.impl.UserServiceImpl;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/users")
public class UserControllerImpl implements IBaseController<UserDetailDto, Long, UserServiceImpl> {
    @Resource
    private UserServiceImpl userService;

    @Override
    public UserServiceImpl getService() {
        return this.userService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        return userService.createPasswordResetTokenForUser(email);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        return userService.changePassword(changePasswordDto);
    }
}