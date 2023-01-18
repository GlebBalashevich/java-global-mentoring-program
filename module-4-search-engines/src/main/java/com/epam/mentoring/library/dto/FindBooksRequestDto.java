package com.epam.mentoring.library.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindBooksRequestDto {

    private String field;

    private String value;

    private String facetField;

    private Boolean fulltext;

    private String query;

}
