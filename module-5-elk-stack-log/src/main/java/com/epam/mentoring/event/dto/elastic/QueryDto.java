package com.epam.mentoring.event.dto.elastic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryDto {

    @JsonProperty("bool")
    private BoolQueryDto booleanQuery;

}
