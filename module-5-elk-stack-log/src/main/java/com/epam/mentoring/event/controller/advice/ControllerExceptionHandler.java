package com.epam.mentoring.event.controller.advice;

import java.time.Instant;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.epam.mentoring.event.dto.exception.BaseExceptionResponse;
import com.epam.mentoring.event.exception.BaseException;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error occurred, contact server administrator";

    private static final String INTERNAL_SERVER_ERROR_CODE = "ISE-0";

    private static final String BAD_REQUEST_ERROR_CODE = "BRE-0";

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseExceptionResponse> handle(BaseException e, HttpServletRequest request) {
        return new ResponseEntity<>(buildBaseExceptionResponse(e.getHttpStatus(), e.getErrorCode(), e.getMessage(),
                request), e.getHttpStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseExceptionResponse> handle(ConstraintViolationException e, HttpServletRequest request) {
        final var httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(buildBaseExceptionResponse(HttpStatus.BAD_REQUEST, BAD_REQUEST_ERROR_CODE,
                e.getMessage(), request), httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseExceptionResponse> handle(MethodArgumentNotValidException e, HttpServletRequest request) {
        final var httpStatus = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(buildBaseExceptionResponse(httpStatus, BAD_REQUEST_ERROR_CODE,
                e.getMessage(), request), httpStatus);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<BaseExceptionResponse> handle(Throwable e, HttpServletRequest request) {
        log.error("Error occurred while processing request", e);
        final var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(buildBaseExceptionResponse(httpStatus, INTERNAL_SERVER_ERROR_CODE,
                INTERNAL_SERVER_ERROR_MESSAGE, request), httpStatus);
    }

    private BaseExceptionResponse buildBaseExceptionResponse(HttpStatus status, String errorCode, String message,
            HttpServletRequest request) {
        return BaseExceptionResponse.builder()
                .timestamp(Instant.now())
                .error(status.getReasonPhrase())
                .code(errorCode)
                .message(message)
                .path(request.getServletPath())
                .build();
    }

}
