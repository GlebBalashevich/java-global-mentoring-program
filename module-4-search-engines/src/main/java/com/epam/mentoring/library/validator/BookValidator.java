package com.epam.mentoring.library.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.epam.mentoring.library.dto.FindBooksRequestDto;
import com.epam.mentoring.library.exception.BookException;
import com.epam.mentoring.library.util.ErrorCode;

@Slf4j
@Component
public class BookValidator {

    private static final String EMPTY_QUERY_MESSAGE = "Full text search enabled, Query should not be empty";

    private static final String EMPTY_VALUE_MESSAGE = "Field filtering, Value should not be empty";

    public void validateFindBooksRequest(FindBooksRequestDto findBooksRequestDto) {
        if (Boolean.TRUE.equals(findBooksRequestDto.getFulltext())) {
            if (!StringUtils.hasText(findBooksRequestDto.getQuery())) {
                log.error(EMPTY_QUERY_MESSAGE + " request:{}", findBooksRequestDto);
                throw new BookException(EMPTY_QUERY_MESSAGE, HttpStatus.BAD_REQUEST, ErrorCode.BOOK_BAD_REQUEST_ERROR);
            }
        } else {
            if (StringUtils.hasText(findBooksRequestDto.getField())
                    && !StringUtils.hasText(findBooksRequestDto.getValue())) {
                log.error(EMPTY_VALUE_MESSAGE + " request:{}", findBooksRequestDto);
                throw new BookException(EMPTY_VALUE_MESSAGE, HttpStatus.BAD_REQUEST, ErrorCode.BOOK_BAD_REQUEST_ERROR);
            }
        }
    }

}
