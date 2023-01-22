package com.epam.mentoring.library.exception;

import org.springframework.http.HttpStatus;

public class EpubFileReaderException extends BaseException {

    public EpubFileReaderException(String message, Throwable cause, HttpStatus httpStatus, String errorCode) {
        super(message, cause, httpStatus, errorCode);
    }

}
