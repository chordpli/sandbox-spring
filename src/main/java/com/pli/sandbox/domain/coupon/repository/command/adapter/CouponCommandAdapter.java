package com.pli.sandbox.domain.coupon.repository.command.adapter;

import com.pli.sandbox.domain.coupon.model.Coupon;
import com.pli.sandbox.domain.coupon.repository.CouponJpaRepository;
import com.pli.sandbox.domain.coupon.repository.command.CouponCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponCommandAdapter implements CouponCommandRepository {
    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }
}
