package com.epam.mentoring.event.exception;

import org.springframework.http.HttpStatus;

public class ElasticException extends BaseException {

    public ElasticException(String message, Throwable cause, HttpStatus httpStatus,
            String errorCode) {
        super(message, cause, httpStatus, errorCode);
    }

    public ElasticException(String message, HttpStatus httpStatus, String errorCode) {
        super(message, httpStatus, errorCode);
    }

}
