package com.pli.sandbox.common.exception.resultcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthCode implements ResultCodeProvider {
    UNAUTHORIZED("UNAUTHORIZED", "인증되지 않은 사용자입니다."),
    FORBIDDEN("FORBIDDEN", "접근 권한이 없습니다."),
    REFRESH_TOKEN_EXPIRED("REFRESH_TOKEN_EXPIRED", "리프레시 토큰이 만료되었습니다."),
    MISSING_AUTHORIZATION_HEADER("MISSING_AUTHORIZATION_HEADER", "Authorization 헤더가 없습니다."),
    INVALID_SUBJECT_FORMAT("INVALID_SUBJECT_FORMAT", "주어진 포맷과 일치하지 않는 형식입니다.");

    private final String code;
    private final String message;
}
