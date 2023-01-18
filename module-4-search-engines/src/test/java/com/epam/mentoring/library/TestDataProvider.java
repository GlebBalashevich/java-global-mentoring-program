package com.epam.mentoring.library;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import org.apache.solr.client.solrj.response.SuggesterResponse;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.springframework.data.solr.core.query.SimpleField;
import org.springframework.data.solr.core.query.SolrPageRequest;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.SimpleFacetFieldEntry;
import org.springframework.data.solr.core.query.result.SolrResultPage;

import com.epam.mentoring.library.dto.BookDto;
import com.epam.mentoring.library.dto.FacetDto;
import com.epam.mentoring.library.dto.FindBooksRequestDto;
import com.epam.mentoring.library.dto.FindBooksResponseDto;
import com.epam.mentoring.library.dto.SuggestionDto;
import com.epam.mentoring.library.dto.SuggestionResponseDto;
import com.epam.mentoring.library.model.Book;

public class TestDataProvider {

    private TestDataProvider() {
    }

    public static Book getBookStub() {
        return Book.builder()
                .id("008e6bbc-7ad3-4a8c-a3c0-658aad072c67")
                .title("book title")
                .authors(List.of("author1", "author2"))
                .content("book content")
                .language("en")
                .build();
    }

    public static BookDto getBookDtoStub() {
        BookDto bookDto = new BookDto();
        bookDto.setId("008e6bbc-7ad3-4a8c-a3c0-658aad072c67");
        bookDto.setTitle("book title");
        bookDto.setAuthors(List.of("author1", "author2"));
        bookDto.setContent("book content");
        bookDto.setLanguage("en");
        return bookDto;
    }

    public static FindBooksRequestDto getFindBooksRequestDtoStub() {
        return FindBooksRequestDto.builder()
                .field("field")
                .value("value")
                .facetField("facetField")
                .fulltext(true)
                .query("query")
                .build();
    }

    public static FindBooksResponseDto getFindBooksResponseDtoStub() {
        return FindBooksResponseDto.builder()
                .books(List.of(getBookDtoStub()))
                .facets(Map.of("authors", List.of(FacetDto.builder().valueCount(1L).value("author1").build())))
                .numFound(1L)
                .build();
    }

    public static FacetPage<Book> getFacetPageBookStub() {
        final var facetPage = new SolrResultPage<>(List.of(getBookStub()), new SolrPageRequest(0, 1), 1,
                null);
        facetPage.addAllFacetFieldResultPages(Map.of(new SimpleField("authors"),
                new SolrResultPage<>(List.of(new SimpleFacetFieldEntry(new SimpleField("authors"), "author1", 1)))));
        return facetPage;
    }

    public static SuggesterResponse getSuggestResponseStub() {
        SimpleOrderedMap<Object> suggestion = new SimpleOrderedMap<>();
        suggestion.add("term", "author1");
        suggestion.add("weight", 0L);
        suggestion.add("payload", "");
        SimpleOrderedMap<Object> query = new SimpleOrderedMap<>();
        query.add("numFound", 1);
        query.add("suggestions", List.of(suggestion));
        SimpleOrderedMap<Object> autocomplete = new SimpleOrderedMap<>();
        autocomplete.add("auth", query);
        SimpleOrderedMap<NamedList<Object>> suggest = new SimpleOrderedMap<>();
        suggest.add("autocomplete", autocomplete);
        return new SuggesterResponse(suggest);
    }

    public static SuggestionResponseDto getSuggestionResponseDtoStub() {
        final var suggestionResponseDto = new SuggestionResponseDto();
        suggestionResponseDto.setInput("auth");
        suggestionResponseDto.setSuggestions(Map.of("autocomplete", List.of(getSuggestionDtoStub())));
        return suggestionResponseDto;
    }

    public static SuggestionDto getSuggestionDtoStub() {
        final var suggestionDto = new SuggestionDto();
        suggestionDto.setTerm("author1");
        return suggestionDto;
    }

    public static List<nl.siegmann.epublib.domain.Book> getEpubBooksStub() {
        final var book = new nl.siegmann.epublib.domain.Book();
        book.setMetadata(getEpubMetadataStub());
        book.setSpine(getEpubSpineStub());
        return List.of(book);
    }

    public static Metadata getEpubMetadataStub() {
        final var metadata = new Metadata();
        metadata.addAuthor(new Author("author1"));
        metadata.addAuthor(new Author("author2"));
        metadata.addTitle("book title");
        metadata.setLanguage("en");
        return metadata;
    }

    public static Spine getEpubSpineStub() {
        final var spine = new Spine();
        byte[] content = "book content".getBytes(StandardCharsets.UTF_8);
        final var resource = new Resource(content, "href");
        spine.addSpineReference(new SpineReference(resource));
        return spine;
    }

}
