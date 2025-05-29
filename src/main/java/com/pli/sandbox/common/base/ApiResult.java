package com.pli.sandbox.common.base;

import com.pli.sandbox.common.exception.CustomException;
import com.pli.sandbox.common.exception.resultcode.ResultCodeProvider;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResult<T> {

    private String code;
    private String message;
    private T data;

    public static ApiResult<Object> of(final CustomException e) {
        return of(e.getResultCode(), e.getData());
    }

    public static <T> ApiResult<T> of(final ResultCodeProvider resultCode, final T data) {
        return ApiResult.<T>builder()
                .code(resultCode.getCode())
                .message(resultCode.getMessage())
                .data(data)
                .build();
    }

    public static ApiResult<?> ok() {
        return ok(null);
    }

    public static <T> ApiResult<T> ok(final T data) {
        return of("SUCCESS_NORMAL", "성공", data);
    }

    public static ApiResult<String> badRequest(String message) {
        return of("BAD_REQUEST", message, null);
    }

    public static ApiResult<List<ValidationErrorResponse>> badRequest(List<ValidationErrorResponse> validationErrors) {
        return of("INVALIDATE_REQUEST", "요청 값이 잘 못 되었습니다.", validationErrors);
    }

    private static <T> ApiResult<T> of(final String code, final String message, final T data) {
        return ApiResult.<T>builder().code(code).message(message).data(data).build();
    }
}
