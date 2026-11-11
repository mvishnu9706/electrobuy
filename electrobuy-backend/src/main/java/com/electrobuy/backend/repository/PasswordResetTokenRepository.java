package com.electrobuy.backend.repository;

import com.electrobuy.backend.entity.PasswordResetToken;
import com.electrobuy.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    // 🚀 NEW: Allows us to find an existing token using the User object
    Optional<PasswordResetToken> findByUser(User user);
}