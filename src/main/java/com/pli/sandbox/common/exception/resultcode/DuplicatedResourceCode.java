package com.pli.sandbox.common.exception.resultcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DuplicatedResourceCode implements ResultCodeProvider {
    USER_EMAIL("DUPLICATED_USER_EMAIL", "유저 이메일"),
    USER_NICKNAME("DUPLICATED_USER_NICKNAME", "유저 닉네임");

    private final String code;
    private final String message;
}
