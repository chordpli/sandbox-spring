package com.pli.sandbox.common.exception.resultcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotFoundCode implements ResultCodeProvider {
    RESOURCE_NOT_FOUND("NOT_FOUND_RESOURCE", "리소스를 찾을 수 없습니다."),
    USER_NOT_FOUND_BY_EMAIL("NOT_FOUND_USER_BY_EMAIL", "해당 이메일의 사용자를 찾을 수 없습니다."),
    USER_NOT_FOUND_BY_ID("NOT_FOUND_USER_BY_ID", "해당 ID의 사용자를 찾을 수 없습니다.");

    private final String code;
    private final String message;
}
