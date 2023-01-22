package com.epam.mentoring.library.exception;

import org.springframework.http.HttpStatus;

public class BookException extends BaseException {

    public BookException(String message, HttpStatus httpStatus, String errorCode) {
        super(message, httpStatus, errorCode);
    }

}
