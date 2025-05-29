package com.pli.programhub.domain.users.repository.query;

import com.pli.programhub.domain.users.model.User;
import java.util.Optional;

public interface UserQueryRepository {
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);
}
