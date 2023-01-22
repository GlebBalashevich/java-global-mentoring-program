package com.epam.mentoring.library.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class SuggestionResponseDto {

    private String input;

    private Map<String, List<SuggestionDto>> suggestions;

}
