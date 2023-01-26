package com.epam.mentoring.event.dto.elastic;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryRequestBodyDto {

    private QueryDto query;

}
