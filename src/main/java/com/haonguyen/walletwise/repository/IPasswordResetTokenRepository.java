package com.haonguyen.walletwise.repository;

import com.haonguyen.walletwise.model.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
    PasswordResetToken findByUserId(Long userId);
}
