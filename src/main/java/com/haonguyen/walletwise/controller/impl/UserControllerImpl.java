package com.haonguyen.walletwise.controller.impl;

import com.haonguyen.walletwise.controller.IBaseController;
import com.haonguyen.walletwise.model.dto.UserDetailDto;
import com.haonguyen.walletwise.service.impl.UserServiceImpl;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/users")
public class UserControllerImpl implements IBaseController<UserDetailDto, Long, UserServiceImpl> {
    @Resource
    @Getter
    private UserServiceImpl userService;

    @Override
    public UserServiceImpl getService() {
        return this.userService;
    }
}