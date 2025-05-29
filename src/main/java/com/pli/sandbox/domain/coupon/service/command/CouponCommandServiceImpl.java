package com.pli.sandbox.domain.coupon.service.command;

import com.pli.sandbox.domain.coupon.enums.CouponType;
import com.pli.sandbox.domain.coupon.model.Coupon;
import com.pli.sandbox.domain.coupon.repository.CouponJpaRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponCommandServiceImpl implements CouponCommandService {

    private final CouponJpaRepository couponJpaRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void issueWelcomeCoupon(Long userId) {
        Instant now = Instant.now();
        Instant endDate = now.plus(30, ChronoUnit.DAYS);

        Coupon welcomeCoupon = Coupon.createOf(
                generateCouponCode(),
                "신규 회원 웰컴 쿠폰",
                CouponType.WELCOME,
                "신규 가입을 축하합니다! 30일 내에 사용 가능한 10% 할인 쿠폰입니다.",
                10,
                false,
                now,
                endDate,
                userId);

        couponJpaRepository.save(welcomeCoupon);
    }

    private String generateCouponCode() {
        return "WELCOME-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
