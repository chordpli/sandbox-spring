package com.pli.sandbox.domain.coupon.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCoupon is a Querydsl query type for Coupon
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCoupon extends EntityPathBase<Coupon> {

    private static final long serialVersionUID = -1454141570L;

    public static final QCoupon coupon = new QCoupon("coupon");

    public final com.pli.sandbox.common.entity.QAbstractJpaPersistable _super = new com.pli.sandbox.common.entity.QAbstractJpaPersistable(this);

    public final StringPath code = createString("code");

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.Instant> deletedAt = _super.deletedAt;

    //inherited
    public final NumberPath<Long> deletedBy = _super.deletedBy;

    public final StringPath description = createString("description");

    public final NumberPath<Integer> discountRate = createNumber("discountRate", Integer.class);

    public final DateTimePath<java.time.Instant> endDate = createDateTime("endDate", java.time.Instant.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath isUsed = createBoolean("isUsed");

    public final StringPath name = createString("name");

    public final DateTimePath<java.time.Instant> startDate = createDateTime("startDate", java.time.Instant.class);

    public final EnumPath<com.pli.sandbox.domain.coupon.enums.CouponType> type = createEnum("type", com.pli.sandbox.domain.coupon.enums.CouponType.class);

    //inherited
    public final DateTimePath<java.time.Instant> updatedAt = _super.updatedAt;

    //inherited
    public final NumberPath<Long> updatedBy = _super.updatedBy;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QCoupon(String variable) {
        super(Coupon.class, forVariable(variable));
    }

    public QCoupon(Path<? extends Coupon> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCoupon(PathMetadata metadata) {
        super(Coupon.class, metadata);
    }

}

