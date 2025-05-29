package com.pli.sandbox.common.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractJpaPersistable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint")
    private final Long id = 0L;

    @Comment("")
    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    protected Long createdBy = 0L;

    @Comment("생성일시")
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    protected Instant createdAt;

    @Comment("")
    @LastModifiedBy
    @Column(name = "updated_by", nullable = false)
    protected Long updatedBy = 0L;

    @Comment("수정일시")
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    protected Instant updatedAt;

    @Comment("")
    @Column(name = "deleted_by", nullable = true)
    private Long deletedBy;

    @Comment("삭제일시")
    @Column(name = "deleted_at", nullable = true)
    private Instant deletedAt;

    public Long getId() {
        return this.id;
    }

    public void delete() {
        this.deletedBy = 0L;
        this.deletedAt = Instant.now();
    }

    public void delete(Long deletedBy) {
        this.deletedBy = deletedBy;
        this.deletedAt = Instant.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AbstractJpaPersistable that = (AbstractJpaPersistable) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
