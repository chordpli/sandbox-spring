package com.pli.sandbox.common.base;

import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.FieldError;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Getter
@Builder
@RequiredArgsConstructor
public class ValidationErrorResponse {
    private final String field;
    private final String message;

    public static ValidationErrorResponse of(final FieldError fieldError) {
        return ValidationErrorResponse.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .build();
    }

    public static ValidationErrorResponse createNotNull(final String field) {
        return ValidationErrorResponse.builder()
                .field(field)
                .message(String.format("%s 필드는 필수 입니다.", field))
                .build();
    }

    public static ValidationErrorResponse of(final MethodArgumentTypeMismatchException e) {
        String requiredType = Optional.ofNullable(e.getRequiredType())
                .map(Class::getSimpleName)
                .orElse("");

        return ValidationErrorResponse.builder()
                .field(e.getName())
                .message(String.format("%s 필드는 %s 형식만 입력할 수 있습니다.", e.getName(), requiredType))
                .build();
    }
}
