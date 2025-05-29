package com.pli.sandbox.domain.users.service.command;

import com.pli.sandbox.domain.users.dto.SignUpDto;

public interface UserCommandService {
    String signUp(SignUpDto.Request request);
}
