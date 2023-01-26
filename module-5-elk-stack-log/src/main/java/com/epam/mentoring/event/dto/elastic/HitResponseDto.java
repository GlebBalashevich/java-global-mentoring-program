package com.epam.mentoring.event.dto.elastic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.epam.mentoring.event.model.Event;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HitResponseDto {

    private String id;

    @JsonProperty("_source")
    private Event event;

}
