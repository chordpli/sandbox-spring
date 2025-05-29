package com.pli.sandbox.domain.coupon.dto;

import com.pli.sandbox.domain.coupon.enums.CouponType;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GetCouponDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        private Long couponId;
        private String code;
        private String name;
        private CouponType type;
        private String description;
        private Integer discountRate;
        private Boolean isUsed;
        private Instant startDate;
        private Instant endDate;
    }
}
