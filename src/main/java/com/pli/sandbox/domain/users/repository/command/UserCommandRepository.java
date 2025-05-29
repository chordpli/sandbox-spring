package com.pli.sandbox.domain.users.repository.command;

import com.pli.sandbox.domain.users.model.User;

public interface UserCommandRepository {
    User save(User user);
}
