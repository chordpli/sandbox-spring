package com.pli.sandbox.domain.coupon.repository;

import com.pli.sandbox.domain.coupon.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {}
