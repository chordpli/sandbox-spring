package com.pli.sandbox.domain.coupon.repository.command;

import com.pli.sandbox.domain.coupon.model.Coupon;

public interface CouponCommandRepository {
    Coupon save(Coupon coupon);
}
