package com.pli.sandbox.domain.users.repository.query;

import com.pli.sandbox.domain.users.model.User;
import java.util.Optional;

public interface UserQueryRepository {
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);
}
