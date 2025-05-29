package com.pli.sandbox.common.exception;

import com.pli.sandbox.common.exception.resultcode.NotFoundCode;
import com.pli.sandbox.common.exception.resultcode.ResultCodeProvider;

public class NotFoundException extends CustomException {

    public NotFoundException(ResultCodeProvider resultCode) {
        super(resultCode);
    }

    public NotFoundException(ResultCodeProvider resultCode, Object data) {
        super(resultCode, data);
    }

    public NotFoundException(ResultCodeProvider resultCode, Object data, Throwable cause) {
        super(resultCode, data, cause);
    }

    public NotFoundException() {
        super(NotFoundCode.RESOURCE_NOT_FOUND);
    }

    public NotFoundException(Object data) {
        super(NotFoundCode.RESOURCE_NOT_FOUND, data);
    }
}
