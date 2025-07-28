package com.pli.sandbox.domain.users.repository.command.adapter;

import com.pli.sandbox.domain.users.model.User;
import com.pli.sandbox.domain.users.repository.UserJpaRepository;
import com.pli.sandbox.domain.users.repository.command.UserCommandRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCommandAdapter implements UserCommandRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }
}
