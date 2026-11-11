package com.electrobuy.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Address Entity.
 * Upgraded with @Transient keywords to isolate missing physical table columns.
 * Prevents unknown column errors during inserts as seen in image_79e6ac.png and
 * image_79e7a8.png.
 */
@Entity
@Table(name = "addresses")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address_type", length = 255)
    private String addressType; // e.g., HOME, OFFICE, WAREHOUSE

    @Column(name = "area_street", length = 255)
    private String areaStreet; // Maps correctly to database column from image_79e6ac.png

    @Column(name = "city", length = 255)
    private String city;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Marked @Transient so Hibernate won't search for these missing columns in
    // MySQL
    @Transient
    private String fullName;

    @Transient
    private String phoneNumber;

    @Transient
    private String flatHouseNo;

    @Transient
    private String landmark;

    @Transient
    private String state;

    @Transient
    private String pincode;

    @Transient
    private boolean isDefault = false;

    @Transient
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}