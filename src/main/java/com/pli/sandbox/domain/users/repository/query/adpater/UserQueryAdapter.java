package com.pli.programhub.domain.users.repository.query.adpater;

import com.pli.programhub.domain.users.model.User;
import com.pli.programhub.domain.users.repository.UserJpaRepository;
import com.pli.programhub.domain.users.repository.query.UserQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserQueryAdapter implements UserQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id);
    }
}
