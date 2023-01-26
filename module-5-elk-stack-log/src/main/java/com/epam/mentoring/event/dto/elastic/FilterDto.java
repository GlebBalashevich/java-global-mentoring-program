package com.epam.mentoring.event.dto.elastic;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilterDto {

    private Map<String, RangeDto> range;

}
