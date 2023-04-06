package com.epam.mentoring.event.dto.elastic;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MustDto {

    private Map<String, String> match;

}
