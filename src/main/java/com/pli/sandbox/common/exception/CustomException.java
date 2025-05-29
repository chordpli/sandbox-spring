package com.pli.sandbox.common.exception;

import com.pli.sandbox.common.exception.resultcode.ResultCodeProvider;
import java.io.Serial;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final ResultCodeProvider resultCode;
    private final Object data;

    protected CustomException(final ResultCodeProvider resultCode) {
        this(resultCode, null);
    }

    protected CustomException(final ResultCodeProvider resultCode, final Object data) {
        this(resultCode, data, null);
    }

    public CustomException(final ResultCodeProvider resultCode, final Object data, final Throwable cause) {
        super(cause);
        this.resultCode = resultCode;
        this.data = data;
    }
}
