package com.epam.mentoring.library.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.epam.mentoring.library.dto.FindBooksRequestDto;
import com.epam.mentoring.library.exception.BookException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith({ MockitoExtension.class })
class BookValidatorTest {

    private BookValidator bookValidator;

    @BeforeEach
    void init() {
        bookValidator = new BookValidator();
    }

    @Test
    void testValidateFindBooksRequest_FullText() {
        final var findBooksRequestDto = FindBooksRequestDto.builder()
                .fulltext(true)
                .query("query")
                .build();

        assertDoesNotThrow(() -> bookValidator.validateFindBooksRequest(findBooksRequestDto));
    }

    @Test
    void testValidateFindBooksRequest_FullText_NoQuery_BookException() {
        final var findBooksRequestDto = FindBooksRequestDto.builder()
                .fulltext(true)
                .build();

        assertThatThrownBy(() -> bookValidator.validateFindBooksRequest(findBooksRequestDto)).isInstanceOf(
                BookException.class);
    }

    @Test
    void testValidateFindBooksRequest_FieldSearch() {
        final var findBooksRequestDto = FindBooksRequestDto.builder()
                .field("field")
                .value("value")
                .build();

        assertDoesNotThrow(() -> bookValidator.validateFindBooksRequest(findBooksRequestDto));
    }

    @Test
    void testValidateFindBooksRequest_FieldSearch_NoValue_BookException() {
        final var findBooksRequestDto = FindBooksRequestDto.builder()
                .field("field")
                .build();

        assertThatThrownBy(() -> bookValidator.validateFindBooksRequest(findBooksRequestDto)).isInstanceOf(
                BookException.class);
    }

}
