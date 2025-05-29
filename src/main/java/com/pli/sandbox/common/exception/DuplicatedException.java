package com.pli.sandbox.common.exception;

import com.pli.sandbox.common.exception.resultcode.ResultCodeProvider;

public class DuplicatedException extends CustomException {

    public DuplicatedException(ResultCodeProvider resultCode) {
        super(resultCode);
    }

    public DuplicatedException(ResultCodeProvider resultCode, Object data) {
        super(resultCode, data);
    }

    public DuplicatedException(ResultCodeProvider resultCode, Object data, Throwable cause) {
        super(resultCode, data, cause);
    }
}
