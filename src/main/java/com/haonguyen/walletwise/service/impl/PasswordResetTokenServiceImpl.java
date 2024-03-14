package com.haonguyen.walletwise.service.impl;

import com.haonguyen.walletwise.model.entity.PasswordResetToken;
import com.haonguyen.walletwise.model.entity.User;
import com.haonguyen.walletwise.repository.IPasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl {
    private final IPasswordResetTokenRepository iPasswordResetTokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetTokenServiceImpl.class);


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

    public boolean isValidToken(Long userId, String token) {
        logger.error("djfhj: " + userId);

        PasswordResetToken tokenInDB = iPasswordResetTokenRepository.findByUserId(userId);
        logger.error("djfhj: " + tokenInDB.getExpiryDate().getTime());

        if (tokenInDB != null && tokenInDB.getToken().equals(token)) {
            // Token found in the database and matches the provided token
            // Now, check if the token has expired
            Date tokenExpiryDate = tokenInDB.getExpiryDate();
            Date currentDate = new Date(System.currentTimeMillis());
            Long remainingTime = tokenExpiryDate.getTime() - currentDate.getTime();
            if (remainingTime >= 0) {
                // If the difference is non-negative, it means the token is not expired yet
                return true; // Return true, token is valid
            }
        }
        return false; // Token not found or expired
    }
}
