package com.epam.mentoring.event.dto.elastic;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoolQueryDto {

    private List<MustDto> must;

    private FilterDto filter;

}
