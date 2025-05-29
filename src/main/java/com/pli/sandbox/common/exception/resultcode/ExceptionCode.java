package com.pli.sandbox.common.exception.resultcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionCode implements ResultCodeProvider {
    UNSUPPORTED_ACCESS_TOKEN("지원되지 않은 엑세스 토큰입니다."),
    UNSUPPORTED_PROVIDER_TYPE("지원되지 않은 프로바이더 타입입니다."),
    EMPTY_JWT_CLAIMS_STRING("JWT 클레임 문자열이 비어 있습니다."),
    INVALID_SUBJECT_FORMAT("JWT 주체의 형식이 유효하지 않습니다.");

    private final String message;
}
