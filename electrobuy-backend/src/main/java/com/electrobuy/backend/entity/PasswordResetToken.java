package com.electrobuy.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(nullable = false)
    private Date expiryDate;

    // Helper constructor to quickly build tokens that expire in 15 minutes
    public PasswordResetToken(String token, User user) {
        this.token = token;
        this.user = user;
        // Current time + 15 minutes (in milliseconds)
        this.expiryDate = new Date(System.currentTimeMillis() + (15 * 60 * 1000));
    }
}