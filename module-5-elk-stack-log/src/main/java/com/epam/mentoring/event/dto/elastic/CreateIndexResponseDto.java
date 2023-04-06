package com.epam.mentoring.event.dto.elastic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateIndexResponseDto {

    private boolean acknowledged;

    @JsonProperty("shards_acknowledged")
    private boolean shardsAcknowledged;

    private String index;

}
