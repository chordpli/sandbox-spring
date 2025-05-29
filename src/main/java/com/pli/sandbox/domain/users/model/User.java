package com.pli.sandbox.domain.users.model;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLRestriction;

import com.pli.sandbox.common.constant.RoleConstants;
import com.pli.sandbox.common.entity.AbstractJpaPersistable;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
@SQLRestriction("deleted_at is null")
public class User extends AbstractJpaPersistable {

    @Comment("이메일")
    @Column(name = "email", length = 128, nullable = false, unique = true)
    private String email;

    @Comment("비밀번호")
    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Comment("닉네임")
    @Column(name = "nickname", length = 128, nullable = false, unique = true)
    private String nickname;

    @Comment("역할")
    @Column(name = "role", length = 128, nullable = false)
    private String role;

    public static User createOf(String email, String password, String nickname) {
        return new User(email, password, nickname, RoleConstants.ROLE_USER);
    }
}
