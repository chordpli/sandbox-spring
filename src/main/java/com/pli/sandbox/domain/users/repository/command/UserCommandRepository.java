package com.pli.sandbox.domain.users.repository.command;

import com.pli.sandbox.domain.users.model.User;
import java.util.Optional;

public interface UserCommandRepository {
    User save(User user);

    Optional<User> findByEmail(String email);
}
