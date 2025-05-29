package com.pli.sandbox.common.exception.resultcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ResultCodeProvider {
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요."),
    INVALID_REQUEST_BODY("INVALID_REQUEST_BODY", "요청 본문 형식이 잘못되었습니다."),
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", "허용되지 않은 HTTP 메서드입니다."),
    TYPE_MISMATCH("TYPE_MISMATCH", "요청 파라미터의 타입이 잘못되었습니다."),
    MISSING_PARAMETER("MISSING_PARAMETER", "필수 요청 파라미터가 누락되었습니다."),
    INVALID_INPUT_VALUE("INVALID_INPUT_VALUE", "입력 값이 유효하지 않습니다."),
    LOCK_ACQUISITION_FAILED("LOCK_ACQUISITION_FAILED", "락 획득에 실패했습니다."),
    LOCK_INTERRUPTED("LOCK_INTERRUPTED", "락 대기 중 인터럽트가 발생했습니다.");

    private final String code;
    private final String message;
}
