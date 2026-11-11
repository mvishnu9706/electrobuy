package com.electrobuy.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vendors") // Updated table naming to match our standardized model
@Data
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Aligned to match our form submission, registration flow, and controllers
    @Column(name = "business_name", nullable = false)
    private String businessName;

    private String contactPersonName;
    private String email;

    @Column(unique = true)
    private String gstNumber;

    private String panCard;

    // Safety status control variable (Resolves your isVendorActive() error)
    @Column(name = "is_vendor_active")
    private boolean isVendorActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Renamed mapping tag to match user linkage conventions across other entities
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Retained your operational relation array catalog maps
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Product> products;
}