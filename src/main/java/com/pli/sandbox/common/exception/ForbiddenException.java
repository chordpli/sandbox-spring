package com.pli.sandbox.common.exception;

import com.pli.sandbox.common.exception.resultcode.AuthCode;
import com.pli.sandbox.common.exception.resultcode.ResultCodeProvider;

public class ForbiddenException extends CustomException {

    public ForbiddenException(ResultCodeProvider resultCode) {
        super(resultCode);
    }

    public ForbiddenException(ResultCodeProvider resultCode, Object data) {
        super(resultCode, data);
    }

    public ForbiddenException(ResultCodeProvider resultCode, Object data, Throwable cause) {
        super(resultCode, data, cause);
    }

    public ForbiddenException() {
        super(AuthCode.FORBIDDEN);
    }

    public ForbiddenException(Object data) {
        super(AuthCode.FORBIDDEN, data);
    }
}
