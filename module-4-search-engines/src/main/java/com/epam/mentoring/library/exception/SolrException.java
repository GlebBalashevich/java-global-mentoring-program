package com.epam.mentoring.library.exception;

import org.springframework.http.HttpStatus;

public class SolrException extends BaseException {

    public SolrException(String message, Throwable cause, HttpStatus httpStatus,
            String errorCode) {
        super(message, cause, httpStatus, errorCode);
    }

}
