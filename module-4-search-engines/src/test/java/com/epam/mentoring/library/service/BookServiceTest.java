package com.epam.mentoring.library.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.epam.mentoring.library.TestDataProvider;
import com.epam.mentoring.library.dto.IndexBooksResponseDto;
import com.epam.mentoring.library.exception.BookException;
import com.epam.mentoring.library.mapper.BookMapper;
import com.epam.mentoring.library.reader.EpubBookReader;
import com.epam.mentoring.library.repository.BookSolrRepository;
import com.epam.mentoring.library.repository.BookSolrTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({ MockitoExtension.class })
class BookServiceTest {

    private BookService bookService;

    @Mock
    private BookSolrRepository bookSolrRepository;

    @Mock
    private BookSolrTemplate bookSolrTemplate;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private EpubBookReader epubBookReader;

    @BeforeEach
    void init() {
        bookService = new BookService(bookSolrRepository, bookSolrTemplate, bookMapper, epubBookReader);
    }

    @Test
    void testIndexBooks() {
        final var epubBooks = TestDataProvider.getEpubBooksStub();
        final var books = List.of(TestDataProvider.getBookStub());
        final var expected = IndexBooksResponseDto.builder().booksIndexed(1).build();

        when(epubBookReader.readEpubBooks()).thenReturn(epubBooks);
        when(bookSolrRepository.findBookByTitle(epubBooks.get(0).getMetadata().getFirstTitle())).thenReturn(
                Optional.empty());
        when(bookMapper.toBook(epubBooks.get(0))).thenReturn(books.get(0));
        when(bookSolrRepository.saveAll(books)).thenReturn(books);

        final var actual = bookService.indexBooks();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testIndexBooks_NoNewIndexingBooks() {
        final var epubBooks = TestDataProvider.getEpubBooksStub();
        final var book = TestDataProvider.getBookStub();
        final var expected = IndexBooksResponseDto.builder().booksIndexed(0).build();

        when(epubBookReader.readEpubBooks()).thenReturn(epubBooks);
        when(bookSolrRepository.findBookByTitle(epubBooks.get(0).getMetadata().getFirstTitle())).thenReturn(
                Optional.of(book));

        final var actual = bookService.indexBooks();

        assertThat(actual).isEqualTo(expected);
        verify(bookSolrRepository, never()).saveAll(any());
    }

    @Test
    void testIndexBooks_NoEpubBooks() {
        final var expected = IndexBooksResponseDto.builder().booksIndexed(0).build();

        when(epubBookReader.readEpubBooks()).thenReturn(Collections.emptyList());

        final var actual = bookService.indexBooks();

        assertThat(actual).isEqualTo(expected);
        verify(bookSolrRepository, never()).findBookByTitle(any());
        verify(bookSolrRepository, never()).saveAll(any());
    }

    @Test
    void testFindBookById() {
        final var book = TestDataProvider.getBookStub();
        final var bookId = "008e6bbc-7ad3-4a8c-a3c0-658aad072c67";
        final var expected = TestDataProvider.getBookDtoStub();

        when(bookSolrRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toBookDto(book)).thenReturn(expected);

        final var actual = bookService.findBookById(bookId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testFindBookById_NotFound() {
        final var bookId = "wrong id";

        when(bookSolrRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.findBookById("wrong id")).isInstanceOf(BookException.class);
    }

    @Test
    void testFindBooksByParameters() {
        final var findBooksRequestDto = TestDataProvider.getFindBooksRequestDtoStub();
        final var facetPage = TestDataProvider.getFacetPageBookStub();
        final var expected = TestDataProvider.getFindBooksResponseDtoStub();

        when(bookSolrTemplate.findBooksByParameters(findBooksRequestDto)).thenReturn(facetPage);
        when(bookMapper.toFindBooksResponseDto(facetPage)).thenReturn(expected);

        final var actual = bookService.findBooksByParameters(findBooksRequestDto);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testRetrieveBookSuggestions() {
        final var query = "auth";
        final var suggestions = TestDataProvider.getSuggestResponseStub();
        final var expected = TestDataProvider.getSuggestionResponseDtoStub();

        when(bookSolrTemplate.getSuggestions(query)).thenReturn(suggestions);
        when(bookMapper.toSuggestionResponseDto(query, suggestions)).thenReturn(expected);

        final var actual = bookService.retrieveBookSuggestions(query);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testRemoveBook() {
        final var book = TestDataProvider.getBookStub();
        final var bookId = "008e6bbc-7ad3-4a8c-a3c0-658aad072c67";

        when(bookSolrRepository.findById(bookId)).thenReturn(Optional.of(book));

        bookService.removeBook(bookId);

        verify(bookSolrRepository).delete(book);
    }

    @Test
    void testRemoveBookById_NotFound() {
        final var bookId = "wrong id";

        when(bookSolrRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.removeBook("wrong id")).isInstanceOf(BookException.class);
    }

}
