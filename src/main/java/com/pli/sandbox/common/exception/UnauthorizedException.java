package com.pli.sandbox.common.exception;

import com.pli.sandbox.common.exception.resultcode.AuthCode;
import com.pli.sandbox.common.exception.resultcode.ResultCodeProvider;

public class UnauthorizedException extends CustomException {

    public UnauthorizedException(ResultCodeProvider resultCode) {
        super(resultCode);
    }

    public UnauthorizedException(ResultCodeProvider resultCode, Object data) {
        super(resultCode, data);
    }

    public UnauthorizedException(ResultCodeProvider resultCode, Object data, Throwable cause) {
        super(resultCode, data, cause);
    }

    public UnauthorizedException() {
        super(AuthCode.UNAUTHORIZED);
    }
}
