package com.epam.mentoring.library.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FacetDto {

    private Long valueCount;

    private String value;

}
