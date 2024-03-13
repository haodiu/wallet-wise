package com.haonguyen.walletwise.service.impl;

import com.haonguyen.walletwise.model.entity.PasswordResetToken;
import com.haonguyen.walletwise.model.entity.User;
import com.haonguyen.walletwise.repository.IPasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl {
    private final IPasswordResetTokenRepository iPasswordResetTokenRepository;

    @Value("${application.email.token.expiration}") long emailTokenExpiration;

    public void save(String token, User user) {
        PasswordResetToken oldToken = iPasswordResetTokenRepository.findByUserId(user.getId());
        if (oldToken != null) {
            iPasswordResetTokenRepository.delete(oldToken);
        }
        PasswordResetToken myToken = new PasswordResetToken();
        myToken.setToken(token);
        myToken.setUser(user);
        myToken.setExpiryDate(new Date(System.currentTimeMillis() + emailTokenExpiration));
        iPasswordResetTokenRepository.save(myToken);
    }
}
