package com.epam.mentoring.library.dto;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindBooksResponseDto {

    private List<BookDto> books;

    private Map<String, List<FacetDto>> facets;

    private Long numFound;

}
