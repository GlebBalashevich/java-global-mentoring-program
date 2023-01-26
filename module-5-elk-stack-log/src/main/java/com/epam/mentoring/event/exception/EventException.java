package com.epam.mentoring.event.exception;

import org.springframework.http.HttpStatus;

public class EventException extends BaseException {

    public EventException(String message, Throwable cause, HttpStatus httpStatus,
            String errorCode) {
        super(message, cause, httpStatus, errorCode);
    }

    public EventException(String message, HttpStatus httpStatus, String errorCode) {
        super(message, httpStatus, errorCode);
    }

}
