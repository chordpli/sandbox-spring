package com.pli.sandbox.domain.users.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserSignUpEvent {
    private final Long userId;
    private final String email;
}
