package com.epam.mentoring.event.dto.elastic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchEventsResponseDto {

    @JsonProperty("hits")
    private TotalHitResponseDto totalHit;

}
