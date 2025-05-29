package com.pli.sandbox.common.advice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.pli.sandbox.common.base.ApiResult;
import com.pli.sandbox.common.base.ValidationErrorResponse;
import com.pli.sandbox.common.exception.BusinessException;
import com.pli.sandbox.common.exception.CustomException;
import com.pli.sandbox.common.exception.DuplicatedException;
import com.pli.sandbox.common.exception.NotFoundException;
import com.pli.sandbox.common.exception.UnauthorizedException;
import com.pli.sandbox.common.exception.resultcode.CommonErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ================= Custom Exceptions ==================

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ApiResult<?> handleNotFoundException(NotFoundException e) {
        log.error(
                "NotFoundException: code={}, message={}, data={}",
                e.getResultCode().getCode(),
                e.getMessage(),
                e.getData(),
                e);
        return ApiResult.of(e.getResultCode(), e.getData());
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(DuplicatedException.class)
    public ApiResult<?> handleDuplicatedException(DuplicatedException e) {
        log.error(
                "DuplicatedException: code={}, message={}, data={}",
                e.getResultCode().getCode(),
                e.getMessage(),
                e.getData(),
                e);
        return ApiResult.of(e.getResultCode(), e.getData());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BusinessException.class)
    public ApiResult<?> handleBusinessException(BusinessException e) {
        log.error(
                "BusinessException: code={}, message={}, data={}",
                e.getResultCode().getCode(),
                e.getMessage(),
                e.getData(),
                e);
        return ApiResult.of(e.getResultCode(), e.getData());
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ApiResult<?> handleUnauthorizedException(UnauthorizedException e) {
        log.error(
                "UnauthorizedException: code={}, message={}, data={}",
                e.getResultCode().getCode(),
                e.getMessage(),
                e.getData(),
                e);
        return ApiResult.of(e.getResultCode(), e.getData());
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CustomException.class)
    public ApiResult<?> handleCustomException(CustomException e) {
        log.error(
                "Unhandled CustomException: code={}, message={}, data={}",
                e.getResultCode().getCode(),
                e.getMessage(),
                e.getData(),
                e);
        return ApiResult.of(CommonErrorCode.INTERNAL_SERVER_ERROR, e.getData());
    }

    // ============= Spring MVC & Servlet Exceptions ===============

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<List<ValidationErrorResponse>> handleValidationExceptions(MethodArgumentNotValidException e) {
        log.warn("Validation failed (RequestBody): {}", e.getMessage());
        List<ValidationErrorResponse> errors = e.getBindingResult().getFieldErrors().stream()
                .map(ValidationErrorResponse::of)
                .collect(Collectors.toList());
        return ApiResult.badRequest(errors);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ApiResult<List<ValidationErrorResponse>> handleBindExceptions(BindException e) {
        log.warn("Binding failed (ModelAttribute): {}", e.getMessage());
        List<ValidationErrorResponse> errors = e.getBindingResult().getFieldErrors().stream()
                .map(ValidationErrorResponse::of)
                .collect(Collectors.toList());
        return ApiResult.badRequest(errors);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResult<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("Invalid request body format: {}", e.getMessage());
        return ApiResult.of(CommonErrorCode.INVALID_REQUEST_BODY, null);
    }

    @ResponseStatus(METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResult<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("Method not allowed: {}", e.getMessage());
        return ApiResult.of(CommonErrorCode.METHOD_NOT_ALLOWED, null);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResult<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("Method argument type mismatch: {}", e.getMessage());
        return ApiResult.badRequest(List.of(ValidationErrorResponse.of(e)));
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResult<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("Missing request parameter: {}", e.getMessage());
        return ApiResult.of(CommonErrorCode.MISSING_PARAMETER, "필수 파라미터 누락: " + e.getParameterName());
    }

    // ================== Fallback Handlers ==================

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ApiResult<?> handleRuntimeException(RuntimeException e) {
        log.error("Unhandled RuntimeException occurred", e);
        return ApiResult.of(CommonErrorCode.INTERNAL_SERVER_ERROR, null);
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResult<?> handleException(Exception e) {
        log.error("Unhandled Exception occurred", e);
        return ApiResult.of(CommonErrorCode.INTERNAL_SERVER_ERROR, null);
    }
}
