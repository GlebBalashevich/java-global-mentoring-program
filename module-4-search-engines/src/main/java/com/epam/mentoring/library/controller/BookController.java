package com.epam.mentoring.library.controller;

import javax.validation.constraints.Size;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epam.mentoring.library.dto.BookDto;
import com.epam.mentoring.library.dto.FindBooksRequestDto;
import com.epam.mentoring.library.dto.FindBooksResponseDto;
import com.epam.mentoring.library.dto.IndexBooksResponseDto;
import com.epam.mentoring.library.dto.SuggestionResponseDto;
import com.epam.mentoring.library.service.BookService;
import com.epam.mentoring.library.validator.BookValidator;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/books")
@RequiredArgsConstructor
@Validated
public class BookController {

    private final BookService bookService;

    private final BookValidator bookValidator;

    @GetMapping("/{id}")
    public BookDto findBookById(@PathVariable String id) {
        log.debug("Requested book by id: {}", id);
        return bookService.findBookById(id);
    }

    @GetMapping
    public FindBooksResponseDto findBooksByParameters(
            @RequestParam(required = false) String field,
            @RequestParam(required = false) String value,
            @RequestParam(required = false) String facetField,
            @RequestParam(required = false) Boolean fulltext,
            @RequestParam(required = false) String query) {
        final var findBooksRequestDto = FindBooksRequestDto.builder()
                .field(field)
                .value(value)
                .facetField(facetField)
                .fulltext(fulltext)
                .query(query).build();
        bookValidator.validateFindBooksRequest(findBooksRequestDto);
        log.debug("Requested search books by params: {}", findBooksRequestDto);
        return bookService.findBooksByParameters(findBooksRequestDto);
    }

    @GetMapping("/suggest")
    public SuggestionResponseDto retrieveBookSuggestions(@RequestParam @Size(min = 3) String query) {
        log.debug("Requested book autosuggestions by query: {}", query);
        return bookService.retrieveBookSuggestions(query);
    }

    @PostMapping("/index")
    public IndexBooksResponseDto indexBooks() {
        log.debug("Requested books indexing");
        return bookService.indexBooks();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBook(@PathVariable String id) {
        log.debug("Requested removing book by id:{}", id);
        bookService.removeBook(id);
    }

}
