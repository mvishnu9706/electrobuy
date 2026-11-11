package com.electrobuy.backend.repository;

import com.electrobuy.backend.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {
}
