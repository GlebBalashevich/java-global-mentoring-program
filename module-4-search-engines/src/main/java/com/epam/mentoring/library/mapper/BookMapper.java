package com.epam.mentoring.library.mapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Spine;
import org.apache.solr.client.solrj.response.SuggesterResponse;
import org.apache.solr.client.solrj.response.Suggestion;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.epam.mentoring.library.dto.BookDto;
import com.epam.mentoring.library.dto.FacetDto;
import com.epam.mentoring.library.dto.FindBooksResponseDto;
import com.epam.mentoring.library.dto.SuggestionDto;
import com.epam.mentoring.library.dto.SuggestionResponseDto;
import com.epam.mentoring.library.exception.EpubFileReaderException;
import com.epam.mentoring.library.model.Book;
import com.epam.mentoring.library.util.ErrorCode;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDto toBookDto(Book book);

    FacetDto toFacetDto(FacetFieldEntry fieldEntry);

    SuggestionResponseDto toSuggestionResponseDto(String input, SuggesterResponse suggesterResponse);

    Map<String, List<SuggestionDto>> toSuggestionDtoMap(Map<String, List<Suggestion>> suggestionsMap);

    List<SuggestionDto> toSuggestionsDtoList(List<Suggestion> suggestionsList);

    default FindBooksResponseDto toFindBooksResponseDto(FacetPage<Book> facetPage) {
        return FindBooksResponseDto.builder()
                .books(toBooksDto(facetPage.getContent()))
                .numFound(facetPage.getTotalElements())
                .facets(toFacetsDto(facetPage.getFacetResultPages()))
                .build();
    }

    default Book toBook(nl.siegmann.epublib.domain.Book epubBook) {
        return Book.builder()
                .id(UUID.randomUUID().toString())
                .title(epubBook.getTitle())
                .authors(toStringAuthors(epubBook.getMetadata().getAuthors()))
                .content(toStringContent(epubBook.getSpine()))
                .language(epubBook.getMetadata().getLanguage())
                .build();
    }

    private List<BookDto> toBooksDto(List<Book> books) {
        return books.stream().map(this::toBookDto).toList();
    }

    private Map<String, List<FacetDto>> toFacetsDto(Collection<Page<FacetFieldEntry>> facets) {
        return CollectionUtils.isEmpty(facets) ? Collections.emptyMap()
                : facets.stream().flatMap(page -> page.getContent().stream())
                        .filter(fieldEntry -> StringUtils.hasText(fieldEntry.getKey().getName()))
                        .collect(Collectors.groupingBy(fieldEntry -> fieldEntry.getKey().getName(),
                                Collectors.mapping(this::toFacetDto, Collectors.toList())));
    }

    private List<String> toStringAuthors(List<Author> authors) {
        return authors.stream().map(author -> (author.getFirstname() + " " + author.getLastname()).trim()).toList();
    }

    private String toStringContent(Spine spine) {
        return spine.getSpineReferences().stream()
                .map(spineReference -> {
                    try {
                        return new String(spineReference.getResource().getData(), StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        throw new EpubFileReaderException("Unable to read file content", e,
                                HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.EPUB_SCANNING_ERROR);
                    }
                })
                .reduce("", (part, element) -> part + element);
    }

}
