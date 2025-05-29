package com.pli.sandbox.domain.coupon.service.query;

import com.pli.sandbox.domain.coupon.dto.GetCouponDto;
import com.pli.sandbox.domain.coupon.repository.query.CouponQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponQueryServiceImpl implements CouponQueryService {

    private final CouponQueryRepository couponQueryRepository;

    @Override
    public Page<GetCouponDto.Response> findAllByUserId(Long userId, Pageable pageable) {
        return couponQueryRepository.findAllByUserId(userId, pageable);
    }
}
