package com.epam.mentoring.library.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.epam.mentoring.library.dto.BookDto;
import com.epam.mentoring.library.dto.FindBooksRequestDto;
import com.epam.mentoring.library.dto.FindBooksResponseDto;
import com.epam.mentoring.library.dto.IndexBooksResponseDto;
import com.epam.mentoring.library.dto.SuggestionResponseDto;
import com.epam.mentoring.library.exception.BookException;
import com.epam.mentoring.library.mapper.BookMapper;
import com.epam.mentoring.library.reader.EpubBookReader;
import com.epam.mentoring.library.repository.BookSolrRepository;
import com.epam.mentoring.library.repository.BookSolrTemplate;
import com.epam.mentoring.library.util.ErrorCode;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private static final String BOOK_NOT_FOUND_MESSAGE = "Book with id:%s not found";

    private final BookSolrRepository bookSolrRepository;

    private final BookSolrTemplate bookSolrTemplate;

    private final BookMapper bookMapper;

    private final EpubBookReader epubBookReader;

    public IndexBooksResponseDto indexBooks() {
        final var indexingBooks = epubBookReader.readEpubBooks().stream()
                .filter(eBook -> bookSolrRepository.findBookByTitle(eBook.getTitle()).isEmpty())
                .map(bookMapper::toBook)
                .toList();
        if (!CollectionUtils.isEmpty(indexingBooks)) {
            bookSolrRepository.saveAll(indexingBooks);
        }
        log.debug("{} books were successfully indexed", indexingBooks.size());
        return IndexBooksResponseDto.builder().booksIndexed(indexingBooks.size()).build();
    }

    public BookDto findBookById(String id) {
        return bookSolrRepository.findById(id)
                .map(bookMapper::toBookDto)
                .orElseThrow(() -> error(id));
    }

    public FindBooksResponseDto findBooksByParameters(FindBooksRequestDto findBooksRequestDto) {
        final var facetPage = bookSolrTemplate.findBooksByParameters(findBooksRequestDto);
        return bookMapper.toFindBooksResponseDto(facetPage);
    }

    public SuggestionResponseDto retrieveBookSuggestions(String query) {
        final var suggestionResponse = bookSolrTemplate.getSuggestions(query);
        return bookMapper.toSuggestionResponseDto(query, suggestionResponse);
    }

    public void removeBook(String id) {
        final var book = bookSolrRepository.findById(id)
                .orElseThrow(() -> error(id));
        bookSolrRepository.delete(book);
        log.debug("Book with id:{} was successfully removed", id);
    }

    private BookException error(String id) {
        log.error(String.format(BOOK_NOT_FOUND_MESSAGE, id));
        return new BookException(String.format(BOOK_NOT_FOUND_MESSAGE, id), HttpStatus.NOT_FOUND,
                ErrorCode.BOOK_NOT_FOUND_ERROR);
    }

}
