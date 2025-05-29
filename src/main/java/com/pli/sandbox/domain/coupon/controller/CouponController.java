package com.pli.sandbox.domain.coupon.controller;

import com.pli.sandbox.domain.coupon.dto.GetCouponDto;
import com.pli.sandbox.domain.coupon.service.query.CouponQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponQueryService couponQueryService;

    @GetMapping
    public ResponseEntity<Page<GetCouponDto.Response>> findAllByUserId(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(couponQueryService.findAllByUserId(userId, PageRequest.of(page, size)));
    }
}
