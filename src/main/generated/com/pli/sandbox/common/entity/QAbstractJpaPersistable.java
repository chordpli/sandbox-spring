package com.pli.sandbox.common.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAbstractJpaPersistable is a Querydsl query type for AbstractJpaPersistable
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QAbstractJpaPersistable extends EntityPathBase<AbstractJpaPersistable> {

    private static final long serialVersionUID = -887473454L;

    public static final QAbstractJpaPersistable abstractJpaPersistable = new QAbstractJpaPersistable("abstractJpaPersistable");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final NumberPath<Long> createdBy = createNumber("createdBy", Long.class);

    public final DateTimePath<java.time.Instant> deletedAt = createDateTime("deletedAt", java.time.Instant.class);

    public final NumberPath<Long> deletedBy = createNumber("deletedBy", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.Instant> updatedAt = createDateTime("updatedAt", java.time.Instant.class);

    public final NumberPath<Long> updatedBy = createNumber("updatedBy", Long.class);

    public QAbstractJpaPersistable(String variable) {
        super(AbstractJpaPersistable.class, forVariable(variable));
    }

    public QAbstractJpaPersistable(Path<? extends AbstractJpaPersistable> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAbstractJpaPersistable(PathMetadata metadata) {
        super(AbstractJpaPersistable.class, metadata);
    }

}

