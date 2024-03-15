package com.haonguyen.walletwise.controller.impl;

import com.haonguyen.walletwise.controller.IBaseController;
import com.haonguyen.walletwise.model.dto.CategoryDto;
import com.haonguyen.walletwise.model.dto.ChangePasswordDto;
import com.haonguyen.walletwise.model.dto.TotalAmountDto;
import com.haonguyen.walletwise.model.dto.UserDetailDto;
import com.haonguyen.walletwise.service.impl.UserServiceImpl;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/users")
public class UserControllerImpl implements IBaseController<UserDetailDto, Long, UserServiceImpl> {
    @Resource
    private UserServiceImpl userService;
    private final static Logger logger = LoggerFactory.getLogger(UserControllerImpl.class);

    @Override
    public UserServiceImpl getService() {
        return this.userService;
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories(
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String sortBy
            ) {
        return userService.getCategories(categoryName, sortBy);
    }

//    @GetMapping("/total-money")
//    public ResponseEntity<List<TotalAmountDto>> getTotalAmount(
//            @RequestParam(required = false) String start,
//            @RequestParam(required = false) String end
//            ) {
//        logger.error("Hello");
//        return userService.computeTotalAmount(start, end);
//    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        return userService.createPasswordResetTokenForUser(email);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        return userService.changePassword(changePasswordDto);
    }
}