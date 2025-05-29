package com.pli.sandbox.domain.coupon.repository.query.adapter;

import static com.pli.sandbox.domain.coupon.model.QCoupon.coupon;

import com.pli.sandbox.domain.coupon.dto.GetCouponDto;
import com.pli.sandbox.domain.coupon.repository.CouponJpaRepository;
import com.pli.sandbox.domain.coupon.repository.query.CouponQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponQueryAdapter implements CouponQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Page<GetCouponDto.Response> findAllByUserId(Long userId, Pageable pageable) {

        List<GetCouponDto.Response> coupons = jpaQueryFactory
                .select(Projections.constructor(
                        GetCouponDto.Response.class,
                        coupon.id,
                        coupon.code,
                        coupon.name,
                        coupon.type,
                        coupon.description,
                        coupon.discountRate,
                        coupon.isUsed,
                        coupon.startDate,
                        coupon.endDate))
                .from(coupon)
                .where(coupon.userId.eq(userId), coupon.deletedAt.isNull())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory.select(coupon.count()).from(coupon);

        return PageableExecutionUtils.getPage(coupons, pageable, countQuery::fetchOne);
    }
}
