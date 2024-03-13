package com.haonguyen.walletwise.service.impl;

import com.haonguyen.walletwise.exception.NotFoundException;
import com.haonguyen.walletwise.model.entity.Role;
import com.haonguyen.walletwise.repository.IRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl {
    private final IRoleRepository iRoleRepository;

    public Role findByName(String roleName) {
        return iRoleRepository.findByName(roleName).orElseThrow(() -> new NotFoundException("Role not found"));
    }
}
