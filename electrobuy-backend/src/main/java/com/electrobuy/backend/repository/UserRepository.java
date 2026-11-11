package com.electrobuy.backend.repository;

import com.electrobuy.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Finds a user by their unique email address (useful for login/auth cycles)
    Optional<User> findByEmail(String email);

    // ✅ Generates an optimized internal boolean validation check to catch email
    // duplication
    boolean existsByEmail(String email);
}