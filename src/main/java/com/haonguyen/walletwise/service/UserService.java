package com.haonguyen.walletwise.service;


import com.haonguyen.walletwise.model.entity.User;
import com.haonguyen.walletwise.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final IUserRepository userRepository;

    public User loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow();
    }
}