package com.pli.sandbox.domain.coupon.model;

import com.pli.sandbox.common.entity.AbstractJpaPersistable;
import com.pli.sandbox.domain.coupon.enums.CouponType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coupons")
@AttributeOverride(name = "id", column = @Column(name = "coupon_id"))
@SQLRestriction("deleted_at is null")
public class Coupon extends AbstractJpaPersistable {

    @Comment("쿠폰 코드")
    @Column(name = "code", length = 128, nullable = false, unique = true)
    private String code;

    @Comment("쿠폰 이름")
    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Comment("쿠폰 타입")
    @Column(name = "type", length = 128, nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponType type;

    @Comment("쿠폰 설명")
    @Column(name = "description", length = 128, nullable = false)
    private String description;

    @Comment("쿠폰 할인률")
    @Column(name = "discount_rate", nullable = false)
    private Integer discountRate;

    @Comment("쿠폰 사용 여부")
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed;

    @Comment("쿠폰 사용 시작일")
    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Comment("쿠폰 사용 종료일")
    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @Comment("사용자")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    public static Coupon createOf(
            String code,
            String name,
            CouponType type,
            String description,
            Integer discountRate,
            Boolean isUsed,
            Instant startDate,
            Instant endDate,
            Long userId) {
        return new Coupon(code, name, type, description, discountRate, isUsed, startDate, endDate, userId);
    }
}
