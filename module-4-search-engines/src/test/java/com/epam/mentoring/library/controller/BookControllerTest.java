package com.epam.mentoring.library.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.epam.mentoring.library.TestDataProvider;
import com.epam.mentoring.library.dto.FindBooksRequestDto;
import com.epam.mentoring.library.reader.EpubBookReader;
import com.epam.mentoring.library.repository.BookSolrRepository;
import com.epam.mentoring.library.repository.BookSolrTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@AutoConfigureWebTestClient(timeout = "PT10S")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {

    private static final String URL_TEMPLATE = "/api/v1/books/";

    @MockBean
    private BookSolrRepository bookSolrRepository;

    @MockBean
    private BookSolrTemplate bookSolrTemplate;

    @MockBean
    EpubBookReader epubBookReader;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testFindBookById_Ok() throws IOException {
        final var bookId = UUID.randomUUID().toString();

        when(bookSolrRepository.findById(bookId)).thenReturn(Optional.of(TestDataProvider.getBookStub()));

        final var expectedJson = IOUtils
                .toString(Objects.requireNonNull(this.getClass()
                        .getClassLoader()
                        .getResourceAsStream("expected/bookdto-response.json")), StandardCharsets.UTF_8);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .pathSegment(bookId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(expectedJson);
    }

    @Test
    void testFindBookById_BookNotFound() {
        final var bookId = UUID.randomUUID().toString();

        when(bookSolrRepository.findById(bookId)).thenReturn(Optional.empty());

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .pathSegment(bookId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testFindBooksByParameters_Ok() throws IOException {
        final var findBooksRequestDto = FindBooksRequestDto.builder()
                .field("book field")
                .value("field value")
                .facetField("facet field")
                .fulltext(true)
                .query("fulltext query")
                .build();
        when(bookSolrTemplate.findBooksByParameters(findBooksRequestDto)).thenReturn(
                TestDataProvider.getFacetPageBookStub());

        final var expectedJson = IOUtils
                .toString(Objects.requireNonNull(this.getClass()
                        .getClassLoader()
                        .getResourceAsStream("expected/book-find-request-dto-response.json")), StandardCharsets.UTF_8);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .queryParam("field", findBooksRequestDto.getField())
                        .queryParam("value", findBooksRequestDto.getValue())
                        .queryParam("facetField", findBooksRequestDto.getFacetField())
                        .queryParam("fulltext", findBooksRequestDto.getFulltext())
                        .queryParam("query", findBooksRequestDto.getQuery())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(expectedJson);
    }

    @Test
    void testFindBooksByParameters_NoValue_BadRequest() throws IOException {
        final var findBooksRequestDto = FindBooksRequestDto.builder()
                .field("book field")
                .facetField("facet field")
                .fulltext(false)
                .query("fulltext query")
                .build();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .queryParam("field", findBooksRequestDto.getField())
                        .queryParam("facetField", findBooksRequestDto.getFacetField())
                        .queryParam("fulltext", findBooksRequestDto.getFulltext())
                        .queryParam("query", findBooksRequestDto.getQuery())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testRetrieveBookSuggestions_Ok() throws IOException {
        final var query = "auth";

        when(bookSolrTemplate.getSuggestions(query)).thenReturn(TestDataProvider.getSuggestResponseStub());

        final var expectedJson = IOUtils
                .toString(Objects.requireNonNull(this.getClass()
                        .getClassLoader()
                        .getResourceAsStream("expected/book-suggestion-response.json")), StandardCharsets.UTF_8);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .pathSegment("suggest")
                        .queryParam("query", query)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(expectedJson);
    }

    @Test
    void testRetrieveBookSuggestions_ShortQuery_BadRequest() throws IOException {
        final var query = "at";

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .pathSegment("suggest")
                        .queryParam("query", query)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testIndexBooks_Ok() throws IOException {
        final var epubBooks = TestDataProvider.getEpubBooksStub();
        final var books = List.of(TestDataProvider.getBookStub());
        when(epubBookReader.readEpubBooks()).thenReturn(epubBooks);
        when(bookSolrRepository.findBookByTitle(epubBooks.get(0).getMetadata().getFirstTitle())).thenReturn(
                Optional.empty());
        when(bookSolrRepository.saveAll(books)).thenReturn(books);

        final var expectedJson = IOUtils
                .toString(Objects.requireNonNull(this.getClass()
                        .getClassLoader()
                        .getResourceAsStream("expected/index-books-response.json")), StandardCharsets.UTF_8);

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .pathSegment("index")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(expectedJson);
    }

    @Test
    void testIndexBooks_NoBooksToIndex_Ok() throws IOException {
        when(epubBookReader.readEpubBooks()).thenReturn(Collections.emptyList());

        final var expectedJson = IOUtils
                .toString(Objects.requireNonNull(this.getClass()
                        .getClassLoader()
                        .getResourceAsStream("expected/index-books-no-booksresponse.json")), StandardCharsets.UTF_8);

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .pathSegment("index")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody().json(expectedJson);

        verify(bookSolrRepository, never()).findBookByTitle(any());
        verify(bookSolrRepository, never()).saveAll(any());
    }

    @Test
    void testRemoveBook_Ok() {
        final var bookId = UUID.randomUUID().toString();
        final var book = TestDataProvider.getBookStub();

        when(bookSolrRepository.findById(bookId)).thenReturn(Optional.of(book));
        doNothing().when(bookSolrRepository).delete(book);

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .pathSegment(bookId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testRemoveBook_BookNotFound() {
        final var bookId = UUID.randomUUID().toString();

        when(bookSolrRepository.findById(bookId)).thenReturn(Optional.empty());

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(URL_TEMPLATE)
                        .pathSegment(bookId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

}
