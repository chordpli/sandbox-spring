package com.pli.sandbox.common.exception;

import com.pli.sandbox.common.exception.resultcode.ResultCodeProvider;

public class BusinessException extends CustomException {
    public BusinessException(ResultCodeProvider resultCode) {
        super(resultCode);
    }

    public BusinessException(ResultCodeProvider resultCode, Object data) {
        super(resultCode, data);
    }

    public BusinessException(ResultCodeProvider resultCode, Object data, Throwable cause) {
        super(resultCode, data, cause);
    }
}
