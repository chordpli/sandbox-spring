package com.pli.sandbox.domain.coupon.repository.query;

import com.pli.sandbox.domain.coupon.dto.GetCouponDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponQueryRepository {
    Page<GetCouponDto.Response> findAllByUserId(Long userId, Pageable pageable);
}
