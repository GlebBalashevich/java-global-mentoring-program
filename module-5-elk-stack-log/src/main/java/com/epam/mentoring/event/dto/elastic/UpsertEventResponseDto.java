package com.epam.mentoring.event.dto.elastic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpsertEventResponseDto {

    @JsonProperty("_id")
    private String id;

    private String result;

    @JsonProperty("_version")
    private int version;

}
